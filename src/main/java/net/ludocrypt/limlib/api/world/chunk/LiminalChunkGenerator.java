package net.ludocrypt.limlib.api.world.chunk;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GenerationStep.Carver;
import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public abstract class LiminalChunkGenerator extends ChunkGenerator {

	public LiminalChunkGenerator(BiomeSource biomeSource) {
		super(biomeSource, (biome) -> GenerationSettings.INSTANCE);
	}

	@Override
	public void carve(ChunkRegion chunkRegion, long seed, RandomState randomState, BiomeAccess biomeAccess,
			StructureManager structureManager, Chunk chunk, Carver generationStep) {
	}

	@Override
	public void buildSurface(ChunkRegion region, StructureManager structureManager, RandomState randomState, Chunk chunk) {
	}

	@Override
	public void populateEntities(ChunkRegion region) {
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, RandomState randomState,
			StructureManager structureManager, Chunk chunk) {
		throw new UnsupportedOperationException("Vanilla populateNoise should never be called in LiminalChunkGenerator");
	}

	/**
	 * The number of neighboring chunks which can be accessed for block placement. A
	 * value of 0 means that only this chunk is accessible. A positive value means
	 * that the given amount of neighbors are accessible in each direction. A
	 * negative value means that this region shouldn't be used for block placement.
	 */
	public abstract int getPlacementRadius();

	/**
	 * An extention of the base populateNoise method but with more variables. Use
	 * ChunkRegion as opposed to world when setting blocks, as it allows you to
	 * extend through multiple chunks in {@link getPlacementRadius} away.
	 */
	public abstract CompletableFuture<Chunk> populateNoise(ChunkRegion chunkRegion, ChunkStatus targetStatus,
			Executor executor, ServerWorld world, ChunkGenerator generator,
			StructureTemplateManager structureTemplateManager, ServerLightingProvider lightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> fullChunkConverter, List<Chunk> chunks,
			Chunk chunk);

	@Override
	public int getSeaLevel() {
		return 0;
	}

	@Override
	public int getMinimumY() {
		return 0;
	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world, RandomState randomState) {
		return this.getWorldHeight();
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int y, HeightLimitView world, RandomState random) {
		BlockState[] states = new BlockState[world.getHeight()];

		for (int i = 0; i < states.length; i++) {
			states[i] = Blocks.AIR.getDefaultState();
		}

		return new VerticalBlockSample(0, states);
	}

}
