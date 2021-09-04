package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import net.ludocrypt.corners.world.biome.YearningCanalBiome;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class CornerBiomes {

	public static final RegistryKey<Biome> YEARNING_CANAL = get("yearning_canal", YearningCanalBiome.create());

	public static void init() {

	}

}
