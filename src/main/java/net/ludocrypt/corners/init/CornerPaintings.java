package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import net.ludocrypt.corners.util.DimensionalPaintingVariant;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CornerPaintings {

	public static final PaintingVariant OVERWORLD = get("overworld", DimensionalPaintingVariant.create(48, 48, World.OVERWORLD, DimensionalPaintingVariant.overworldPaintingTarget));
	public static final PaintingVariant OVERWORLD_THIN = get("overworld_thin", DimensionalPaintingVariant.create(16, 32, World.OVERWORLD, DimensionalPaintingVariant.overworldPaintingTarget));
	public static final PaintingVariant OVERWORLD_WIDE = get("overworld_wide", DimensionalPaintingVariant.create(64, 32, World.OVERWORLD, DimensionalPaintingVariant.overworldPaintingTarget));
	public static final PaintingVariant YEARNING_CANAL = get("yearning_canal", DimensionalPaintingVariant.create(48, 48, CornerWorlds.YEARNING_CANAL.getWorldKey(), new Vec3d(5.5D, 1.0D, 5.5D)));
	public static final PaintingVariant COMMUNAL_CORRIDORS = get("communal_corridors", DimensionalPaintingVariant.create(32, 32, CornerWorlds.COMMUNAL_CORRIDORS.getWorldKey(), (player, painting) -> player.getPos().subtract(new Vec3d(player.getX() % 8.0D, player.getY(), player.getZ() % 8.0D)).add(2.0D, 2.0D, 2.0D)));
	public static final PaintingVariant HOARY_CROSSROADS = get("hoary_crossroads", DimensionalPaintingVariant.create(32, 48, CornerWorlds.HOARY_CROSSROADS.getWorldKey(), (player, painting) -> player.getPos().subtract(new Vec3d(player.getX() % 1024.0D, player.getY(), player.getZ() % 1024.0D)).add(512.0D, 263.0D, 0.0D).add(4.0D, 0, 4.0D)));

	public static void init() {

	}

}
