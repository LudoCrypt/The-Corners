package net.ludocrypt.corners.block;

import net.ludocrypt.corners.entity.CornerBoatEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class CornerBoatItem extends BoatItem {

	private final CornerBoatEntity.CornerBoat boatData;
	private final boolean chest;

	public CornerBoatItem(boolean chest, CornerBoatEntity.CornerBoat boatData, Item.Settings settings) {
		super(chest, BoatEntity.Variant.OAK, settings);
		this.chest = chest;
		this.boatData = boatData;
	}

	@Override
	protected BoatEntity createBoatEntity(World world, HitResult hitResult) {
		var entity = boatData.factory(chest).create(boatData.entityType(chest), world);
		entity.updatePosition(hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z);
		return entity;
	}

}
