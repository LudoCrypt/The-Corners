package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.corners.block.RailingBlock;
import net.ludocrypt.corners.init.CornerBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

@Mixin(Block.class)
public class BlockMixin {

	@Inject(method = "Lnet/minecraft/block/Block;shouldDrawSide(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
	private static void corners$shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos otherPos, CallbackInfoReturnable<Boolean> ci) {
		BlockState stateFrom = world.getBlockState(otherPos);
		if (state.isOf(CornerBlocks.DARK_RAILING)) {
			boolean shouldDrawSide = true;
			if (state.get(RailingBlock.LAYERS) > 0) {
				if (stateFrom.getBlock() instanceof RailingBlock) {
					shouldDrawSide = stateFrom.get(RailingBlock.LAYERS) <= state.get(RailingBlock.LAYERS);
				}
			}
			ci.setReturnValue(shouldDrawSide);
		}
	}

}
