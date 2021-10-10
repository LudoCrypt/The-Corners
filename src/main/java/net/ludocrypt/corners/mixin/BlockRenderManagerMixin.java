package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.corners.access.BlockRenderManagerAccess;
import net.ludocrypt.corners.client.render.sky.RemoveSkyboxQuadsBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;

@Mixin(BlockRenderManager.class)
public class BlockRenderManagerMixin implements BlockRenderManagerAccess {

	@Shadow
	@Final
	private BlockModels models;

	@Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
	private void corners$getModel(BlockState state, CallbackInfoReturnable<BakedModel> ci) {
		ci.setReturnValue(new RemoveSkyboxQuadsBakedModel(ci.getReturnValue()));
	}

	@Override
	public BakedModel getModelPure(BlockState state) {
		return models.getModel(state);
	}

}
