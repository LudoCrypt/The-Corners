package net.ludocrypt.corners.mixin;

import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Lists;

import net.ludocrypt.corners.access.ItemRendererAccess;
import net.ludocrypt.corners.client.render.sky.RemoveSkyboxQuadsBakedModel;
import net.ludocrypt.corners.client.render.sky.SkyboxShaders;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements ItemRendererAccess {

	@Unique
	private boolean isPure = false;

	@Override
	public BakedModel getItemModelPure(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed) {
		isPure = true;
		BakedModel model = this.getHeldItemModel(stack, world, entity, seed);
		isPure = false;
		return model;
	}

	@Inject(method = "getHeldItemModel", at = @At("RETURN"), cancellable = true)
	public void corners$getHeldItemModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> ci) {
		if (!isPure) {
			ci.setReturnValue(new RemoveSkyboxQuadsBakedModel(ci.getReturnValue()));
		}
	}

	@Inject(method = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderBakedItemModel(Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;)V", shift = Shift.AFTER))
	public void corners$renderItem(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
		BakedModel bakedModel = ((ItemRendererAccess) (ItemRenderer) (Object) this).getItemModelPure(stack, null, null, 0);
		List<BakedQuad> quads = Lists.newArrayList();
		SkyboxShaders.addAll(quads, bakedModel, null);
		for (Direction dir : Direction.values()) {
			SkyboxShaders.addAll(quads, bakedModel, null, dir);
		}
		Iterator<BakedQuad> quadIterator = quads.iterator();

		while (quadIterator.hasNext()) {
			BakedQuad quad = quadIterator.next();
			Matrix4f matrix = matrices.peek().getModel();
			SkyboxShaders.SKYBOX_CORE_SHADER.findUniformMat4("TransformMatrix").set(matrix);
			VertexConsumer consumer = vertexConsumers.getBuffer(SkyboxShaders.SKYBOX_CORE_SHADER.getRenderLayer(SkyboxShaders.SKYBOX_RENDER_LAYER.apply(new Identifier(quad.getSprite().getId().getNamespace(), "textures/" + quad.getSprite().getId().getPath()))));
			SkyboxShaders.quad(consumer, matrix, quad);
		}
	}

	@Shadow
	public abstract BakedModel getHeldItemModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed);

}
