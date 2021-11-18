package net.ludocrypt.corners.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer;
import net.ludocrypt.corners.client.render.sky.RemoveSkyboxQuadsBakedModel;
import net.minecraft.client.render.model.BakedModel;

@Mixin(BlockRenderer.class)
public class BlockRendererMixin {

	@Unique
	private BakedModel originalModel = null;

	@Group(name = "first")
	@ModifyVariable(method = "Lme/jellysquid/mods/sodium/client/render/pipeline/BlockRenderer;renderModel(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/render/model/BakedModel;Lme/jellysquid/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;ZJ)Z", at = @At("HEAD"), index = 5, allow = 1, remap = false)
	private BakedModel corners$turnSkyboxModel$first$mapped(BakedModel in) {
		return corners$turnSkyboxModel$first(in);
	}

	@Group(name = "first")
	@ModifyVariable(method = "Lme/jellysquid/mods/sodium/client/render/pipeline/BlockRenderer;renderModel(Lnet/minecraft/class_1920;Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;Lnet/minecraft/class_2338;Lnet/minecraft/class_1087;Lme/jellysquid/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;ZJ)Z", at = @At("HEAD"), index = 5, allow = 1, remap = false)
	private BakedModel corners$turnSkyboxModel$first$unmapped(BakedModel in) {
		return corners$turnSkyboxModel$first(in);
	}

	@Group(name = "reset")
	@ModifyVariable(method = "Lme/jellysquid/mods/sodium/client/render/pipeline/BlockRenderer;renderModel(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/render/model/BakedModel;Lme/jellysquid/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;ZJ)Z", at = @At("RETURN"), index = 5, allow = 1, remap = false)
	private BakedModel corners$turnSkyboxModel$reset$mapped(BakedModel in) {
		return corners$turnSkyboxModel$reset(in);
	}

	@Group(name = "reset")
	@ModifyVariable(method = "Lme/jellysquid/mods/sodium/client/render/pipeline/BlockRenderer;renderModel(Lnet/minecraft/class_1920;Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;Lnet/minecraft/class_2338;Lnet/minecraft/class_1087;Lme/jellysquid/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;ZJ)Z", at = @At("RETURN"), index = 5, allow = 1, remap = false)
	private BakedModel corners$turnSkyboxModel$reset$unmapped(BakedModel in) {
		return corners$turnSkyboxModel$reset(in);
	}

	@Unique
	private BakedModel corners$turnSkyboxModel$first(BakedModel in) {
		this.originalModel = in;
		return new RemoveSkyboxQuadsBakedModel(in);
	}

	@Unique
	private BakedModel corners$turnSkyboxModel$reset(BakedModel in) {
		return originalModel;
	}

}
