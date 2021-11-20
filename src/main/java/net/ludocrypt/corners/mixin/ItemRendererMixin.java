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
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.ludocrypt.corners.access.ItemRendererAccess;
import net.ludocrypt.corners.client.render.sky.RemoveSkyboxQuadsBakedModel;
import net.ludocrypt.corners.client.render.sky.SkyboxShaders;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
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

		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.depthMask(true);
		RenderSystem.polygonOffset(3.0F, 3.0F);
		RenderSystem.enablePolygonOffset();

		MinecraftClient client = MinecraftClient.getInstance();
		Camera camera = client.gameRenderer.getCamera();
		MatrixStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.push();
		modelViewStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
		modelViewStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw()));
		modelViewStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
		RenderSystem.applyModelViewMatrix();

		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

		while (quadIterator.hasNext()) {
			BakedQuad quad = quadIterator.next();
			RenderSystem.setShader(() -> SkyboxShaders.SKYBOX_SHADER);
			for (int i = 0; i < 6; i++) {
				RenderSystem.setShaderTexture(i, new Identifier(quad.getSprite().getId().getNamespace(), "textures/" + quad.getSprite().getId().getPath() + "_" + i + ".png"));
			}
			SkyboxShaders.quad((vec3f) -> bufferBuilder.vertex(vec3f.getX(), vec3f.getY(), vec3f.getZ()).next(), matrices.peek().getModel(), quad);
		}

		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.polygonOffset(0.0F, 0.0F);
		RenderSystem.disablePolygonOffset();
		RenderSystem.disableBlend();
		modelViewStack.pop();
		RenderSystem.applyModelViewMatrix();
		RenderSystem.depthMask(true);
	}

	@Shadow
	public abstract BakedModel getHeldItemModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed);

}
