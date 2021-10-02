package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.corners.block.SkyboxBlock;
import net.ludocrypt.corners.block.entity.SkyboxBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

@Mixin(Block.class)
public class BlockMixin {

	@Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
	private static void corners$shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos blockPos, CallbackInfoReturnable<Boolean> ci) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() instanceof SkyboxBlock) {
			if (state.getBlock() instanceof SkyboxBlock) {
				BlockEntity entityA = world.getBlockEntity(pos);
				BlockEntity entityB = world.getBlockEntity(blockPos);
				if (entityA != null && entityB != null) {
					if (entityA instanceof SkyboxBlockEntity skyboxA && entityB instanceof SkyboxBlockEntity skyboxB) {
						ci.setReturnValue(!skyboxA.skyboxId.equals(skyboxB.skyboxId));
					}
				}
			} else {
				ci.setReturnValue(false);
			}
		}
	}

}
