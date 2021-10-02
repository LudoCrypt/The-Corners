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
import net.ludocrypt.corners.world.chunk.SingleBlockChunkGenerator;
import net.ludocrypt.corners.world.chunk.YearningCanalChunkGenerator;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.DirectBiomeAccessType;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

public class CornerWorld {

	// Yearning Canal
	public static final String YEARNING_CANAL = "yearning_canal";
	public static final Identifier YEARNING_CANAL_ID = TheCorners.id(YEARNING_CANAL);
	public static final RegistryKey<Biome> YEARNING_CANAL_BIOME = get("yearning_canal", YearningCanalBiome.create());
	public static final DimensionType YEARNING_CANAL_DIMENSION_TYPE = DimensionType.create(OptionalLong.of(1200), true, false, false, false, 1.0, false, false, true, false, false, 0, CornerConfig.getInstance().condensedDimensions ? 432 : 2032, CornerConfig.getInstance().condensedDimensions ? 432 : 2032, DirectBiomeAccessType.INSTANCE, YEARNING_CANAL_ID, YEARNING_CANAL_ID, 1.0F);
	public static final RegistryKey<DimensionType> YEARNING_CANAL_DIMENSION_TYPE_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, YEARNING_CANAL_ID);
	public static final RegistryKey<DimensionOptions> YEARNING_CANAL_DIMENSION_OPTOINS_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_KEY, YEARNING_CANAL_ID);
	public static final RegistryKey<World> YEARNING_CANAL_WORLD_REGISTRY_KEY = RegistryKey.of(Registry.WORLD_KEY, YEARNING_CANAL_ID);
	public static final SoundEvent YEARNING_CANAL_RADIO = getRadio(YEARNING_CANAL, CornerSoundEvents.RADIO_YEARNING_CANAL);

	// Communal Corridors
	public static final String COMMUNAL_CORRIDORS = "communal_corridors";
	public static final Identifier COMMUNAL_CORRIDORS_ID = TheCorners.id(COMMUNAL_CORRIDORS);
	public static final DimensionType COMMUNAL_CORRIDORS_DIMENSION_TYPE = DimensionType.create(OptionalLong.of(23500), true, false, false, false, 1.0, false, false, true, false, false, 0, 128, 128, DirectBiomeAccessType.INSTANCE, COMMUNAL_CORRIDORS_ID, COMMUNAL_CORRIDORS_ID, 0.075F);
	public static final RegistryKey<Biome> COMMUNAL_CORRIDORS_BIOME = get("communal_corridors", CommunalCorridorsBiome.create());
	public static final RegistryKey<DimensionType> COMMUNAL_CORRIDORS_DIMENSION_TYPE_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, COMMUNAL_CORRIDORS_ID);
	public static final RegistryKey<DimensionOptions> COMMUNAL_CORRIDORS_DIMENSION_OPTOINS_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_KEY, COMMUNAL_CORRIDORS_ID);
	public static final RegistryKey<World> COMMUNAL_CORRIDORS_WORLD_REGISTRY_KEY = RegistryKey.of(Registry.WORLD_KEY, COMMUNAL_CORRIDORS_ID);
	public static final SoundEvent COMMUNAL_CORRIDORS_RADIO = getRadio(COMMUNAL_CORRIDORS, CornerSoundEvents.RADIO_COMMUNAL_CORRIDORS);

	public static void init() {
		RegistryHelper.get("single_block_chunk_generator", SingleBlockChunkGenerator.CODEC);
		RegistryHelper.get("yearning_canal_chunk_generator", YearningCanalChunkGenerator.CODEC);
		RegistryHelper.get("communal_corridors_chunk_generator", CommunalCorridorsChunkGenerator.CODEC);
	}

}
