package net.ludocrypt.corners.init;

import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.entity.DimensionalPaintingEntity;
import net.ludocrypt.corners.entity.covrus.CorvusEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class CornerEntities {

	public static final EntityType<DimensionalPaintingEntity> DIMENSIONAL_PAINTING_ENTITY = get("dimensional_painting", QuiltEntityTypeBuilder.create().entityFactory(DimensionalPaintingEntity::new)
			.setDimensions(new EntityDimensions(0.5F, 0.5F, false)).maxBlockTrackingRange(10).trackingTickInterval(Integer.MAX_VALUE).build());

	public static final EntityType<CorvusEntity> CORVUS_ENTITY = get("corvus", QuiltEntityTypeBuilder.createMob().entityFactory(CorvusEntity::new).spawnGroup(SpawnGroup.AMBIENT)
			.setDimensions(new EntityDimensions(0.375F, 0.875F, false)).maxBlockTrackingRange(10).defaultAttributes(CorvusEntity.createAttributes()).build());

	public static void init() {

	}

	public static <E extends Entity, T extends EntityType<E>> T get(String id, T entity) {
		return Registry.register(Registries.ENTITY_TYPE, TheCorners.id(id), entity);
	}

}
