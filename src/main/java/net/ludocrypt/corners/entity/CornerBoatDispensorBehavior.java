package net.ludocrypt.corners.entity;

import net.minecraft.block.dispenser.BoatDispenserBehavior;
import net.minecraft.entity.vehicle.BoatEntity;

public final class CornerBoatDispensorBehavior extends BoatDispenserBehavior {

	private final CornerBoatEntity.CornerBoat boatData;

	public CornerBoatDispensorBehavior(CornerBoatEntity.CornerBoat boatData, boolean chest) {
		super(BoatEntity.Variant.OAK, chest);
		this.boatData = boatData;
	}

	public CornerBoatEntity.CornerBoat getBoatData() {
		return boatData;
	}

}
