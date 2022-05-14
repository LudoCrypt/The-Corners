package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.corners.block.RailingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

@Mixin(Block.class)
public class BlockMixin {

	@Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
	private static void shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos otherPos, CallbackInfoReturnable<Boolean> ci) {
		if (state.getBlock() instanceof RailingBlock) {
			if (state.get(RailingBlock.LAYERS) > 0) {
				for (Direction dir : new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST }) {
					if (side.equals(dir)) {
						if (world.getBlockState(pos.offset(dir)).getBlock() instanceof RailingBlock) {
							ci.setReturnValue(world.getBlockState(pos.offset(dir)).get(RailingBlock.LAYERS) < state.get(RailingBlock.LAYERS));
						}
					}
				}
			}
		}
	}

}
