package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;
import static net.ludocrypt.corners.util.RegistryHelper.getRadio;

import java.util.OptionalLong;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.config.CornerConfig;
import net.ludocrypt.corners.util.RegistryHelper;
import net.ludocrypt.corners.world.biome.CommunalCorridorsBiome;
import net.ludocrypt.corners.world.biome.YearningCanalBiome;
import net.ludocrypt.corners.world.chunk.CommunalCorridorsChunkGenerator;
import net.ludocrypt.corners.world.chunk.YearningCanalChunkGenerator;
import net.ludocrypt.limlib.api.world.LiminalWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionType;

public class CornerWorld {

	public static final RegistryKey<Biome> YEARNING_CANAL_BIOME = get("yearning_canal", YearningCanalBiome.create());
	public static final RegistryKey<Biome> COMMUNAL_CORRIDORS_BIOME = get("communal_corridors", CommunalCorridorsBiome.create());

	public static final LiminalWorld YEARNING_CANAL = get("yearning_canal", new LiminalWorld(TheCorners.id("yearning_canal"), DimensionType.create(OptionalLong.of(1200), true, false, false, true, 1.0, false, false, true, false, false, 0, CornerConfig.getInstance().condensedDimensions ? 432 : 2032, CornerConfig.getInstance().condensedDimensions ? 432 : 2032, TheCorners.id("yearning_canal"), TheCorners.id("yearning_canal"), 1.0F), (biomeRegistry, seed) -> new YearningCanalChunkGenerator(new FixedBiomeSource(biomeRegistry.getOrThrow(CornerWorld.YEARNING_CANAL_BIOME)), seed)));
	public static final LiminalWorld COMMUNAL_CORRIDORS = get("communal_corridors", new LiminalWorld(TheCorners.id("communal_corridors"), DimensionType.create(OptionalLong.of(23500), true, false, false, true, 1.0, false, false, true, false, false, 0, 128, 128, TheCorners.id("communal_corridors"), TheCorners.id("communal_corridors"), 0.075F), (biomeRegistry, seed) -> new CommunalCorridorsChunkGenerator(new FixedBiomeSource(biomeRegistry.getOrThrow(CornerWorld.COMMUNAL_CORRIDORS_BIOME)), seed)));

	public static void init() {
		RegistryHelper.get("yearning_canal_chunk_generator", YearningCanalChunkGenerator.CODEC);
		RegistryHelper.get("communal_corridors_chunk_generator", CommunalCorridorsChunkGenerator.CODEC);
		getRadio(YEARNING_CANAL.world, CornerSoundEvents.RADIO_YEARNING_CANAL);
		getRadio(COMMUNAL_CORRIDORS.world, CornerSoundEvents.RADIO_COMMUNAL_CORRIDORS);
	}

}
