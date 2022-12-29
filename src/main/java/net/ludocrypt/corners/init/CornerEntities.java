package net.ludocrypt.corners.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.entity.DimensionalPaintingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

public class CornerEntities {

	public static final EntityType<DimensionalPaintingEntity> DIMENSIONAL_PAINTING_ENTITY = get("dimensional_painting", FabricEntityTypeBuilder.create().entityFactory(DimensionalPaintingEntity::new).dimensions(new EntityDimensions(0.5F, 0.5F, false)).trackRangeBlocks(10).trackedUpdateRate(Integer.MAX_VALUE).build());

	public static void init() {

	}

	public static <E extends Entity, T extends EntityType<E>> T get(String id, T entity) {
		return Registry.register(Registry.ENTITY_TYPE, TheCorners.id(id), entity);
	}

}
