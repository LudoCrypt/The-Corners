package net.ludocrypt.corners.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer;
import net.ludocrypt.corners.client.render.sky.RemoveSkyboxQuadsBakedModel;
import net.minecraft.client.render.model.BakedModel;

@Mixin(BlockRenderer.class)
public class BlockRendererMixin {

	@Unique
	private BakedModel originalModel = null;

	@ModifyVariable(method = "Lme/jellysquid/mods/sodium/client/render/pipeline/BlockRenderer;renderModel(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/render/model/BakedModel;Lme/jellysquid/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;ZJ)Z", at = @At("HEAD"), index = 5)
	private BakedModel corners$turnSkyboxModel$first(BakedModel in) {
		this.originalModel = in;
		return new RemoveSkyboxQuadsBakedModel(in);
	}

	@ModifyVariable(method = "Lme/jellysquid/mods/sodium/client/render/pipeline/BlockRenderer;renderModel(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/render/model/BakedModel;Lme/jellysquid/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;ZJ)Z", at = @At("RETURN"), index = 5)
	private BakedModel corners$turnSkyboxModel$reset(BakedModel in) {
		return originalModel;
	}

}
