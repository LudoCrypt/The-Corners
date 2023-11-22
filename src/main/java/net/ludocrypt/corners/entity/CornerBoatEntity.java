package net.ludocrypt.corners.entity;

import java.util.function.Supplier;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.World;

public class CornerBoatEntity extends BoatEntity {

	private final CornerBoat boatData;

	public CornerBoatEntity(EntityType<? extends BoatEntity> type, World world, CornerBoat boatData) {
		super(type, world);
		this.boatData = boatData;
	}

	public CornerBoat getBoatData() {
		return boatData;
	}

	@Override
	public BoatEntity.Variant getVariant() {
		return BoatEntity.Variant.OAK;
	}

	@Override
	public void setVariant(BoatEntity.Variant type) {
	}

	@Override
	public Item asItem() {
		return boatData.boat().asItem();
	}

	public static BoatEntity copy(BoatEntity original, CornerBoat boatData) {
		var chest = original instanceof ChestBoatEntity;
		var boat = boatData.factory(chest).create(boatData.entityType(chest), original.getWorld());
		boat.updatePosition(original.getX(), original.getY(), original.getZ());
		return boat;
	}

	@SuppressWarnings("deprecation")
	public enum CornerBoat implements StringIdentifiable {

		GAIA("gaia", () -> CornerBlocks.GAIA_PLANKS, () -> CornerBlocks.GAIA_BOAT, () -> CornerBlocks.GAIA_CHEST_BOAT,
				() -> CornerEntities.GAIA_BOAT, () -> CornerEntities.GAIA_CHEST_BOAT);

		private final String name;
		private final Supplier<ItemConvertible> planks;
		private final Supplier<ItemConvertible> boat;
		private final Supplier<ItemConvertible> chestBoat;
		private final Supplier<EntityType<BoatEntity>> entityType;
		private final Supplier<EntityType<BoatEntity>> chestEntityType;
		public static final StringIdentifiable.EnumCodec<CornerBoat> CODEC = StringIdentifiable
			.createCodec(CornerBoatEntity.CornerBoat::values);

		CornerBoat(String name, Supplier<ItemConvertible> planks, Supplier<ItemConvertible> boat,
				Supplier<ItemConvertible> chestBoat, Supplier<EntityType<BoatEntity>> entityType,
				Supplier<EntityType<BoatEntity>> chestEntityType) {
			this.name = name;
			this.planks = planks;
			this.boat = boat;
			this.chestBoat = chestBoat;
			this.entityType = entityType;
			this.chestEntityType = chestEntityType;
		}

		public ItemConvertible planks() {
			return planks.get();
		}

		public ItemConvertible boat() {
			return boat.get();
		}

		public ItemConvertible chestBoat() {
			return chestBoat.get();
		}

		public EntityType<BoatEntity> entityType(boolean chest) {
			return chest ? chestEntityType.get() : entityType.get();
		}

		public static CornerBoat getType(String name) {
			return CODEC.getOrElse(name, GAIA);
		}

		public EntityType.EntityFactory<BoatEntity> factory(boolean chest) {
			return (type, world) -> chest ? new CornerChestBoatEntity(type, world, this)
					: new CornerBoatEntity(type, world, this);
		}

		public Identifier id() {
			return TheCorners.id(name);
		}

		@Override
		public String asString() {
			return name;
		}

	}

}
