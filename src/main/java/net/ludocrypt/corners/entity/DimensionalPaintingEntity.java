package net.ludocrypt.corners.entity;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.init.CornerEntities;
import net.ludocrypt.corners.mixin.AbstractDecorationEntityAccessor;
import net.ludocrypt.corners.util.DimensionalPaintingMotive;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class DimensionalPaintingEntity extends PaintingEntity {

	public static boolean isPaingintTeleport = false;

	public DimensionalPaintingEntity(EntityType<? extends DimensionalPaintingEntity> type, World world) {
		super(type, world);
	}

	public static DimensionalPaintingEntity create(World world, BlockPos pos) {
		DimensionalPaintingEntity entity = new DimensionalPaintingEntity(CornerEntities.DIMENSIONAL_PAINTING_ENTITY, world);
		entity.attachmentPos = pos;
		return entity;
	}

	public static DimensionalPaintingEntity create(World world, BlockPos pos, Direction direction, PaintingMotive motive) {
		if (motive instanceof DimensionalPaintingMotive) {
			DimensionalPaintingEntity entity = create(world, pos);
			entity.motive = motive;
			entity.setFacing(direction);
			return entity;
		}
		TheCorners.LOGGER.warn("PaintingMotive {} is not DimensionalPaintingMotive, has nowhere to go!", motive);
		throw new UnsupportedOperationException();
	}

	public static PaintingEntity createRegular(World world, BlockPos pos, Direction direction, PaintingMotive motive) {
		PaintingEntity entity = new PaintingEntity(CornerEntities.DIMENSIONAL_PAINTING_ENTITY, world);
		entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
		entity.motive = motive;
		((AbstractDecorationEntityAccessor) entity).callSetFacing(direction);
		return entity;
	}

	public static PaintingEntity createFromMotive(World world, BlockPos pos, Direction direction, PaintingMotive motive) {
		if (motive instanceof DimensionalPaintingMotive) {
			return create(world, pos, direction, motive);
		} else {
			return createRegular(world, pos, direction, motive);
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		super.onPlayerCollision(player);
		if (this.motive instanceof DimensionalPaintingMotive) {
			Box box = this.getBoundingBox().expand(0.3D);
			if (box.contains(player.getEyePos()) && box.contains(player.getPos()) && box.contains(player.getPos().add(0.0D, player.getHeight(), 0.0D))) {
				if (this.world instanceof ServerWorld && player instanceof ServerPlayerEntity spe) {
					ServerWorld world = player.getServer().getWorld(this.getMotive().dimension.apply(spe, this));
					TeleportTarget teleportTarget = this.getMotive().teleportTarget.apply(spe, this);
					DimensionalPaintingEntity.isPaingintTeleport = true;
					FabricDimensions.teleport(spe, world, teleportTarget);
				}
			}
		}
	}

	public DimensionalPaintingMotive getMotive() {
		return (DimensionalPaintingMotive) this.motive;
	}

}
