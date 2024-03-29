package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.corners.entity.DimensionalPaintingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void corners$interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> ci) {

		if (!player.getWorld().isClient) {

			if (((Entity) (Object) this) instanceof PaintingEntity painting) {

				if (!(((Entity) (Object) this) instanceof DimensionalPaintingEntity)) {

					if (Registries.PAINTING_VARIANT.getId(painting.getVariant().value()).getNamespace().equals("corners")) {

						if (player.getStackInHand(hand).getItem().equals(Items.FLINT_AND_STEEL)) {
							DimensionalPaintingEntity dimensional = DimensionalPaintingEntity
								.create(painting.getWorld(), painting.getDecorationBlockPos(),
									painting.getHorizontalFacing(), painting.getVariant().value());
							painting.getWorld().spawnEntity(dimensional);
							painting
								.getWorld()
								.playSound(null, painting.getBlockPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE,
									SoundCategory.BLOCKS, 1.0F, 1.0F);
							player
								.getStackInHand(hand)
								.damage(1, player,
									(playerConsumer) -> playerConsumer
										.sendEquipmentBreakStatus(
											hand.equals(Hand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
							discard();
							ci.setReturnValue(ActionResult.SUCCESS);
						}

					}

				}

			}

		}

	}

	@Shadow
	public abstract void discard();

}
