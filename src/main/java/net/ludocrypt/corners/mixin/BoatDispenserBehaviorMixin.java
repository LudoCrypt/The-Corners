package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.ludocrypt.corners.entity.CornerBoatDispensorBehavior;
import net.ludocrypt.corners.entity.CornerBoatEntity;
import net.minecraft.block.dispenser.BoatDispenserBehavior;
import net.minecraft.entity.vehicle.BoatEntity;

@Mixin(BoatDispenserBehavior.class)
public class BoatDispenserBehaviorMixin {

	@ModifyVariable(method = "dispenseSilently", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;setVariant(Lnet/minecraft/entity/vehicle/BoatEntity$Variant;)V"), allow = 1)
	private BoatEntity corners$modifyBoat(BoatEntity original) {

		if ((Object) this instanceof CornerBoatDispensorBehavior boat) {
			return CornerBoatEntity.copy(original, boat.getBoatData());
		}

		return original;
	}

}
