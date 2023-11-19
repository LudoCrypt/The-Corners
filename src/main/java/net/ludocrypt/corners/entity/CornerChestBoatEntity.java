package net.ludocrypt.corners.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public final class CornerChestBoatEntity extends ChestBoatEntity implements CornerBoatWithData {

	private final CornerBoatEntity.CornerBoat boatData;

	public CornerChestBoatEntity(EntityType<? extends BoatEntity> entityType, World world, CornerBoatEntity.CornerBoat boatData) {
		super(entityType, world);
		this.boatData = boatData;
	}

	@Override
	public CornerBoatEntity.CornerBoat getBoatData() {
		return boatData;
	}

	@Override
	public BoatEntity.Variant getVariant() {
		return BoatEntity.Variant.OAK;
	}

	@Override
	public void setVariant(BoatEntity.Variant type) {}

	@Override
	public Item asItem() {
		return boatData.chestBoat().asItem();
	}

}
