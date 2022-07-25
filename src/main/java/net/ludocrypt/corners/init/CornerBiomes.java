package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;
import static net.ludocrypt.corners.util.RegistryHelper.getMaze;
import static net.ludocrypt.corners.util.RegistryHelper.getRadio;

import net.ludocrypt.corners.world.biome.CommunalCorridorsBiome;
import net.ludocrypt.corners.world.biome.HoaryCrossroadsBiome;
import net.ludocrypt.corners.world.biome.YearningCanalBiome;
import net.ludocrypt.corners.world.chunk.CommunalCorridorsChunkGenerator;
import net.ludocrypt.corners.world.chunk.HoaryCrossroadsChunkGenerator;
import net.ludocrypt.corners.world.chunk.YearningCanalChunkGenerator;
import net.ludocrypt.corners.world.maze.HoaryCrossroadsMazeGenerator;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class CornerBiomes {

	public static final RegistryKey<Biome> YEARNING_CANAL_BIOME = get("yearning_canal", YearningCanalBiome.create());
	public static final RegistryKey<Biome> COMMUNAL_CORRIDORS_BIOME = get("communal_corridors", CommunalCorridorsBiome.create());
	public static final RegistryKey<Biome> HOARY_CROSSROADS_BIOME = get("hoary_crossroads", HoaryCrossroadsBiome.create());

	public static void init() {
		get("yearning_canal_chunk_generator", YearningCanalChunkGenerator.CODEC);
		get("communal_corridors_chunk_generator", CommunalCorridorsChunkGenerator.CODEC);
		get("hoary_crossroads_chunk_generator", HoaryCrossroadsChunkGenerator.CODEC);
		getMaze("hoary_crossroads_maze_generator", HoaryCrossroadsMazeGenerator.CODEC);
		getRadio("yearning_canal", CornerSoundEvents.RADIO_YEARNING_CANAL);
		getRadio("communal_corridors", CornerSoundEvents.RADIO_COMMUNAL_CORRIDORS);
		getRadio("hoary_crossroads", CornerSoundEvents.RADIO_HOARY_CROSSROADS);
	}

}
