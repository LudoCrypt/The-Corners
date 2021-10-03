package net.ludocrypt.corners.world.chunk;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CommunalCorridorsChunkGenerator extends LiminalChunkGenerator {

	public static final Codec<CommunalCorridorsChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.worldSeed;
		})).apply(instance, instance.stable(CommunalCorridorsChunkGenerator::new));
	});

	public CommunalCorridorsChunkGenerator(BiomeSource biomeSource, long worldSeed) {
		super(biomeSource, worldSeed, "communal_corridors");
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return new CommunalCorridorsChunkGenerator(this.biomeSource, seed);
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {

	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk, ChunkStatus targetStatus, ServerWorld world, ChunkRegion region, ChunkGenerator chunkGenerator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, Unloaded>>> function, List<Chunk> list) {
		if (structures.isEmpty()) {
			store("communal_corridors_1", world);
			store("communal_corridors_2", world);
			store("communal_corridors_3", world);
			store("communal_corridors_4", world);
			store("communal_corridors_5", world);
			store("communal_corridors_6", world);
			store("communal_corridors_7", world);
			store("communal_corridors_8", world);
			store("communal_corridors_9", world);
			store("communal_corridors_10", world);
			store("communal_corridors_11", world);
			store("communal_corridors_12", world);
			store("communal_corridors_13", world);
			store("communal_corridors_14", world);
			store("communal_corridors_15", world);
			store("communal_corridors_16", world);
			store("communal_corridors_17", world);
			store("communal_corridors_18", world);
			store("communal_corridors_19", world);
			store("communal_corridors_20", world);
			store("communal_corridors_decorated_1", world);
			store("communal_corridors_decorated_2", world);
			store("communal_corridors_decorated_3", world);
			store("communal_corridors_decorated_4", world);
			store("communal_corridors_decorated_5", world);
			store("communal_corridors_decorated_6", world);
			store("communal_corridors_decorated_7", world);
			store("communal_corridors_decorated_8", world);
		}

		ChunkPos chunkPos = chunk.getPos();

		for (int x = 0; x < 2; x++) {
			for (int z = 0; z < 2; z++) {
				Random random = new Random(region.getSeed() + MathHelper.hashCode(chunk.getPos().getStartX(), chunk.getPos().getStartZ(), x + z));
				if (random.nextDouble() < 0.75) {
					generateNbt(region, chunkPos.getStartPos().add(x * 8, 0, z * 8), "communal_corridors_" + (random.nextInt(20) + 1));
				} else {
					generateNbt(region, chunkPos.getStartPos().add(x * 8, 0, z * 8), "communal_corridors_decorated_" + (random.nextInt(8) + 1));
				}
			}
		}

		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public int getPlacementRadius() {
		return 1;
	}

}
