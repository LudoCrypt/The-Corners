package net.ludocrypt.corners.init;

import java.util.function.BiFunction;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.entity.DimensionalPaintingEntity;
import net.ludocrypt.corners.util.DimensionalPaintingVariant;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CornerPaintings {

	public static final BiFunction<LivingEntity, DimensionalPaintingEntity, Vec3d> overworldPaintingTarget = (entity,
			painting) -> {

		if (entity instanceof ServerPlayerEntity player) {
			BlockPos pos = player.getSpawnPointPosition();

			if (pos != null) {
				ServerWorld serverWorld = player.getServer().getOverworld();
				return PlayerEntity
					.findRespawnPosition(serverWorld, pos, player.getSpawnAngle(), player.isSpawnPointSet(), true)
					.orElse(Vec3d.ofCenter(player.getServer().getOverworld().getSpawnPos()));
			}

		}

		return Vec3d.ofCenter(entity.getServer().getOverworld().getSpawnPos());

	};
	public static final PaintingVariant OVERWORLD = get("overworld",
		DimensionalPaintingVariant.create(48, 48, World.OVERWORLD, overworldPaintingTarget));
	public static final PaintingVariant OVERWORLD_THIN = get("overworld_thin",
		DimensionalPaintingVariant.create(16, 32, World.OVERWORLD, overworldPaintingTarget));
	public static final PaintingVariant OVERWORLD_WIDE = get("overworld_wide",
		DimensionalPaintingVariant.create(64, 32, World.OVERWORLD, overworldPaintingTarget));
	public static final PaintingVariant YEARNING_CANAL = get("yearning_canal",
		DimensionalPaintingVariant.create(48, 48, CornerWorlds.YEARNING_CANAL_KEY, new Vec3d(5.5D, 1.0D, 5.5D)));
	public static final PaintingVariant COMMUNAL_CORRIDORS = get("communal_corridors",
		DimensionalPaintingVariant
			.create(32, 32, CornerWorlds.COMMUNAL_CORRIDORS_KEY,
				(player, painting) -> player
					.getPos()
					.subtract(new Vec3d(player.getX() % 8.0D, player.getY(), player.getZ() % 8.0D))
					.add(2.0D, 2.0D, 2.0D)));
	public static final PaintingVariant HOARY_CROSSROADS = get("hoary_crossroads",
		DimensionalPaintingVariant
			.create(32, 48, CornerWorlds.HOARY_CROSSROADS_KEY,
				(player, painting) -> player
					.getPos()
					.subtract(new Vec3d(player.getX() % 512.0D, player.getY(), player.getZ() % 512.0D))
					.add(256.0D, 263.0D, 0.0D)
					.add(4.0D, 0, 4.0D)));

	public static void init() {
	}

	public static <T extends PaintingVariant> T get(String id, T painting) {
		return Registry.register(Registries.PAINTING_VARIANT, TheCorners.id(id), painting);
	}

}
