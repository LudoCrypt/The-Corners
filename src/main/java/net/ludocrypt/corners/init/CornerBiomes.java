package net.ludocrypt.corners.init;

import com.mojang.serialization.Codec;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.world.biome.CommunalCorridorsBiome;
import net.ludocrypt.corners.world.biome.HoaryCrossroadsBiome;
import net.ludocrypt.corners.world.biome.YearningCanalBiome;
import net.ludocrypt.corners.world.chunk.CommunalCorridorsChunkGenerator;
import net.ludocrypt.corners.world.chunk.HoaryCrossroadsChunkGenerator;
import net.ludocrypt.corners.world.chunk.YearningCanalChunkGenerator;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CornerBiomes {

	public static final RegistryKey<Biome> YEARNING_CANAL_BIOME = get(CornerWorlds.YEARNING_CANAL, YearningCanalBiome.create());
	public static final RegistryKey<Biome> COMMUNAL_CORRIDORS_BIOME = get(CornerWorlds.COMMUNAL_CORRIDORS, CommunalCorridorsBiome.create());
	public static final RegistryKey<Biome> HOARY_CROSSROADS_BIOME = get(CornerWorlds.HOARY_CROSSROADS, HoaryCrossroadsBiome.create());

	public static void init() {
		get("yearning_canal_chunk_generator", YearningCanalChunkGenerator.CODEC);
		get("communal_corridors_chunk_generator", CommunalCorridorsChunkGenerator.CODEC);
		get("hoary_crossroads_chunk_generator", HoaryCrossroadsChunkGenerator.CODEC);
	}

	public static RegistryKey<Biome> get(String id, Biome biome) {
		Registry.register(BuiltinRegistries.BIOME, TheCorners.id(id), biome);
		return RegistryKey.of(Registry.BIOME_KEY, TheCorners.id(id));
	}

	public static <C extends ChunkGenerator, D extends Codec<C>> D get(String id, D chunkGeneratorCodec) {
		return Registry.register(Registry.CHUNK_GENERATOR, TheCorners.id(id), chunkGeneratorCodec);
	}

}
