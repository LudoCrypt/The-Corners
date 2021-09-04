package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.corners.entity.DimensionalPaintingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.registry.Registry;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void corners$interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> ci) {
		if (((Entity) (Object) this)instanceof PaintingEntity painting) {
			if (Registry.PAINTING_MOTIVE.getId(painting.motive).getNamespace().equals("corners")) {
				if (player.getStackInHand(hand).getItem().equals(Items.FLINT_AND_STEEL)) {
					DimensionalPaintingEntity dimensional = DimensionalPaintingEntity.create(painting.world, painting.getBlockPos(), painting.getHorizontalFacing(), painting.motive);
					painting.world.spawnEntity(dimensional);
					painting.world.playSound(null, painting.getBlockPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
					discard();
					ci.setReturnValue(ActionResult.SUCCESS);
				}
			}
		}
	}

	@Shadow
	public abstract void discard();

}