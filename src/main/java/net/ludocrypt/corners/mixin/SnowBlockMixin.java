package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.corners.init.CornerBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowBlock;
import net.minecraft.item.ItemPlacementContext;

@Mixin(SnowBlock.class)
public class SnowBlockMixin {

	@Inject(method = "Lnet/minecraft/block/SnowBlock;canReplace(Lnet/minecraft/block/BlockState;Lnet/minecraft/item/ItemPlacementContext;)Z", at = @At("RETURN"), cancellable = true)
	private void corners$canReplace(BlockState state, ItemPlacementContext ctx, CallbackInfoReturnable<Boolean> ci) {
		if (ctx.getStack() != null) {
			if (ctx.getStack().getItem().equals(CornerBlocks.DARK_RAILING.asItem())) {
				ci.setReturnValue(true);
			}
		}
	}
}
