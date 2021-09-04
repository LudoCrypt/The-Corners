package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import net.ludocrypt.corners.util.DimensionalPaintingMotive;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CornerPaintings {

	public static final PaintingMotive OVERWORLD = get("overworld", DimensionalPaintingMotive.create(48, 48, World.OVERWORLD, (player, painting) -> {
		BlockPos pos = player.getSpawnPointPosition();
		if (pos != null) {
			ServerWorld serverWorld = player.getServer().getOverworld();
			return PlayerEntity.findRespawnPosition(serverWorld, pos, player.getSpawnAngle(), player.isSpawnPointSet(), true).orElse(Vec3d.ofCenter(player.getServer().getOverworld().getSpawnPos()));
		} else {
			return Vec3d.ofCenter(player.getServer().getOverworld().getSpawnPos());
		}
	}));
	public static final PaintingMotive YEARNING_CANAL = get("yearning_canal", DimensionalPaintingMotive.create(48, 48, CornerWorld.YEARNING_CANAL_WORLD_REGISTRY_KEY, new Vec3d(5.5D, 1.0D, 5.5D)));

	public static void init() {

	}

}
