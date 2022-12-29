package net.ludocrypt.corners.entity;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.init.CornerEntities;
import net.ludocrypt.corners.init.CornerSoundEvents;
import net.ludocrypt.corners.mixin.AbstractDecorationEntityAccessor;
import net.ludocrypt.corners.mixin.PaintingEntityAccessor;
import net.ludocrypt.corners.util.DimensionalPaintingVariant;
import net.ludocrypt.limlib.registry.util.LimlibUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class DimensionalPaintingEntity extends PaintingEntity {

	public DimensionalPaintingEntity(EntityType<? extends DimensionalPaintingEntity> type, World world) {
		super(type, world);
	}

	public static DimensionalPaintingEntity create(World world, BlockPos pos) {
		DimensionalPaintingEntity entity = new DimensionalPaintingEntity(CornerEntities.DIMENSIONAL_PAINTING_ENTITY, world);
		entity.attachmentPos = pos;
		return entity;
	}

	public static DimensionalPaintingEntity create(World world, BlockPos pos, Direction direction, PaintingVariant variant) {
		if (variant instanceof DimensionalPaintingVariant) {
			DimensionalPaintingEntity entity = create(world, pos);
			((PaintingEntityAccessor) entity).callSetVariant(Registry.PAINTING_VARIANT.getHolder(Registry.PAINTING_VARIANT.getKey(variant).get()).get());
			entity.setFacing(direction);
			return entity;
		}
		TheCorners.LOGGER.warn("PaintingVariant {} is not DimensionalPaintingVariant, has nowhere to go!", variant);
		throw new UnsupportedOperationException();
	}

	public static PaintingEntity createRegular(World world, BlockPos pos, Direction direction, PaintingVariant variant) {
		PaintingEntity entity = new PaintingEntity(EntityType.PAINTING, world);
		entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
		((PaintingEntityAccessor) entity).callSetVariant(Registry.PAINTING_VARIANT.getHolder(Registry.PAINTING_VARIANT.getKey(variant).get()).get());
		((AbstractDecorationEntityAccessor) entity).callSetFacing(direction);
		return entity;
	}

	public static PaintingEntity createFromMotive(World world, BlockPos pos, Direction direction, PaintingVariant variant) {
		if (variant instanceof DimensionalPaintingVariant) {
			return create(world, pos, direction, variant);
		} else {
			return createRegular(world, pos, direction, variant);
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		super.onPlayerCollision(player);
		if (this.getVariant().value()instanceof DimensionalPaintingVariant variant) {
			Box box = this.getBoundingBox().expand(0.3D);
			if (box.contains(player.getEyePos()) && box.contains(player.getPos()) && box.contains(player.getPos().add(0.0D, player.getHeight(), 0.0D))) {

				if (this.world instanceof ServerWorld && player instanceof ServerPlayerEntity spe) {
					ServerWorld world = player.getServer().getWorld(variant.dimension.apply(spe, this));

					TeleportTarget teleportTarget = variant.teleportTarget.apply(spe, this);
					LimlibUtil.travelTo(spe, world, teleportTarget, CornerSoundEvents.PAINTING_PORTAL_TRAVEL, 0.25F, world.getRandom().nextFloat() * 0.4F + 0.8F);
				}
			}
		}
	}

}
