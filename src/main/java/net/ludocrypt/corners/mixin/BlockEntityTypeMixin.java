package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.corners.init.CornerBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin<T extends BlockEntity> {

	@SuppressWarnings("unchecked")
	@Inject(method = "Lnet/minecraft/block/entity/BlockEntityType;supports(Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
	private void corners$supports(BlockState state, CallbackInfoReturnable<Boolean> ci) {
		Identifier id = BlockEntityType.getId((BlockEntityType<T>) ((Object) this));

		if (id.equals(BlockEntityType.getId(BlockEntityType.SIGN)) || id
			.equals(BlockEntityType.getId(BlockEntityType.HANGING_SIGN))) {

			if (state.getBlock() == CornerBlocks.GAIA_SIGN || state.getBlock() == CornerBlocks.GAIA_HANGING_SIGN || state
				.getBlock() == CornerBlocks.GAIA_WALL_HANGING_SIGN || state.getBlock() == CornerBlocks.GAIA_WALL_SIGN) {
				ci.setReturnValue(true);
			}

		}

	}

}
