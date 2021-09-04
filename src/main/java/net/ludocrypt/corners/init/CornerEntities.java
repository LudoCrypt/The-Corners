package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.ludocrypt.corners.entity.DimensionalPaintingEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;

public class CornerEntities {

	public static final EntityType<DimensionalPaintingEntity> DIMENSIONAL_PAINTING_ENTITY = get("dimensional_painting", FabricEntityTypeBuilder.create().entityFactory(DimensionalPaintingEntity::new).dimensions(new EntityDimensions(0.5F, 0.5F, false)).trackRangeBlocks(10).trackedUpdateRate(Integer.MAX_VALUE).build());

	public static void init() {

	}

}
