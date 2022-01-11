package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import net.ludocrypt.corners.util.DimensionalPaintingMotive;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CornerPaintings {

	public static final PaintingMotive OVERWORLD = get("overworld", DimensionalPaintingMotive.create(48, 48, World.OVERWORLD, DimensionalPaintingMotive.overworldPaintingTarget));
	public static final PaintingMotive OVERWORLD_THIN = get("overworld_thin", DimensionalPaintingMotive.create(16, 32, World.OVERWORLD, DimensionalPaintingMotive.overworldPaintingTarget));
	public static final PaintingMotive OVERWORLD_WIDE = get("overworld_wide", DimensionalPaintingMotive.create(64, 32, World.OVERWORLD, DimensionalPaintingMotive.overworldPaintingTarget));
	public static final PaintingMotive YEARNING_CANAL = get("yearning_canal", DimensionalPaintingMotive.create(48, 48, CornerWorld.YEARNING_CANAL.worldWorldRegistryKey, new Vec3d(5.5D, 1.0D, 5.5D)));
	public static final PaintingMotive COMMUNAL_CORRIDORS = get("communal_corridors", DimensionalPaintingMotive.create(32, 32, CornerWorld.COMMUNAL_CORRIDORS.worldWorldRegistryKey, (player, painting) -> new Vec3d(player.getBlockX() % 8, 2.0D, player.getBlockZ() % 8).add(player.getX(), 0.0D, player.getZ()).add(-2.0D, 0, -2.0D)));

	public static void init() {

	}

}
