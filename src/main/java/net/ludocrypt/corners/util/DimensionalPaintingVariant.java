package net.ludocrypt.corners.util;

import java.util.function.BiFunction;

import net.ludocrypt.corners.entity.DimensionalPaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class DimensionalPaintingVariant extends PaintingVariant {

	public final RegistryKey<World> radioRedirect;
	public final BiFunction<ServerPlayerEntity, DimensionalPaintingEntity, RegistryKey<World>> dimension;
	public final BiFunction<ServerPlayerEntity, DimensionalPaintingEntity, TeleportTarget> teleportTarget;

	public DimensionalPaintingVariant(int width, int height, RegistryKey<World> radioRedirect,
			BiFunction<ServerPlayerEntity, DimensionalPaintingEntity, RegistryKey<World>> dimension,
			BiFunction<ServerPlayerEntity, DimensionalPaintingEntity, TeleportTarget> teleportTarget) {
		super(width, height);
		this.radioRedirect = radioRedirect;
		this.dimension = dimension;
		this.teleportTarget = teleportTarget;
	}

	public DimensionalPaintingVariant(int width, int height, RegistryKey<World> dimension,
			BiFunction<ServerPlayerEntity, DimensionalPaintingEntity, TeleportTarget> teleportTarget) {
		this(width, height, dimension, (player, painting) -> dimension, teleportTarget);
	}

	public DimensionalPaintingVariant(int width, int height, RegistryKey<World> dimension, TeleportTarget teleport) {
		this(width, height, dimension, (player, painting) -> dimension, (player, painting) -> teleport);
	}

	public static DimensionalPaintingVariant create(int width, int height, RegistryKey<World> dimension,
			BiFunction<ServerPlayerEntity, DimensionalPaintingEntity, Vec3d> teleportTarget) {
		return new DimensionalPaintingVariant(width, height, dimension, (player, painting) -> dimension,
			(player, painting) -> new TeleportTarget(teleportTarget.apply(player, painting), player.getVelocity(),
				player.getYaw(), player.getPitch()));
	}

	public static DimensionalPaintingVariant create(int width, int height, RegistryKey<World> dimension, Vec3d dest) {
		return new DimensionalPaintingVariant(width, height, dimension, (player, painting) -> dimension,
			(player, painting) -> new TeleportTarget(dest, player.getVelocity(), player.getYaw(), player.getPitch()));
	}

}
