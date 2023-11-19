package net.ludocrypt.corners.init;

import com.mojang.serialization.Codec;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.world.chunk.CommunalCorridorsChunkGenerator;
import net.ludocrypt.corners.world.chunk.HoaryCrossroadsChunkGenerator;
import net.ludocrypt.corners.world.chunk.YearningCanalChunkGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

public class CornerBiomes {

	public static final RegistryKey<Biome> YEARNING_CANAL_BIOME = RegistryKey.of(RegistryKeys.BIOME, TheCorners.id(CornerWorlds.YEARNING_CANAL));
	public static final RegistryKey<Biome> COMMUNAL_CORRIDORS_BIOME = RegistryKey.of(RegistryKeys.BIOME, TheCorners.id(CornerWorlds.COMMUNAL_CORRIDORS));
	public static final RegistryKey<Biome> HOARY_CROSSROADS_BIOME = RegistryKey.of(RegistryKeys.BIOME, TheCorners.id(CornerWorlds.HOARY_CROSSROADS));
	public static final RegistryKey<Feature<?>> GAIA_TREE_FEATURE = RegistryKey.of(RegistryKeys.FEATURE, TheCorners.id("gaia_tree"));
	public static final RegistryKey<ConfiguredFeature<?, ?>> CONFIGURED_GAIA_TREE_FEATURE = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, TheCorners.id("gaia_tree"));
	public static final RegistryKey<ConfiguredFeature<?, ?>> CONFIGURED_SAPLING_GAIA_TREE_FEATURE = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, TheCorners.id("gaia_sapling"));

	public static void init() {
		get("yearning_canal_chunk_generator", YearningCanalChunkGenerator.CODEC);
		get("communal_corridors_chunk_generator", CommunalCorridorsChunkGenerator.CODEC);
		get("hoary_crossroads_chunk_generator", HoaryCrossroadsChunkGenerator.CODEC);
	}

	public static <C extends ChunkGenerator, D extends Codec<C>> D get(String id, D chunkGeneratorCodec) {
		return Registry.register(Registries.CHUNK_GENERATOR, TheCorners.id(id), chunkGeneratorCodec);
	}

}
