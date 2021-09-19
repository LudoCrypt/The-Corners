package net.ludocrypt.corners.world.chunk;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class SingleBlockChunkGenerator extends ChunkGenerator {

	public static final Codec<SingleBlockChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BlockState.CODEC.fieldOf("state").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.state;
		}), BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.worldSeed;
		})).apply(instance, instance.stable(SingleBlockChunkGenerator::new));
	});

	public final BlockState state;
	public final long worldSeed;

	public SingleBlockChunkGenerator(BlockState state, BiomeSource biomeSource, long worldSeed) {
		super(biomeSource, biomeSource, new StructuresConfig(false), worldSeed);
		this.state = state;
		this.worldSeed = worldSeed;
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return new SingleBlockChunkGenerator(state, biomeSource, seed);
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {

	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk) {
		return CompletableFuture.supplyAsync(() -> {
			for (int x = chunk.getPos().getStartX(); x < chunk.getPos().getStartX() + 16; x++) {
				for (int z = chunk.getPos().getStartZ(); z < chunk.getPos().getStartZ() + 16; z++) {
					for (int y = chunk.getBottomY(); y < chunk.getTopY(); y++) {
						chunk.setBlockState(new BlockPos(x, y, z), state, false);
					}
				}
			}

			return chunk;
		}, Util.getMainWorkerExecutor());
	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world) {
		return world.getTopY();
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		BlockState[] sample = new BlockState[world.getHeight()];
		for (int i = 0; i < sample.length; i++) {
			sample[i] = state;
		}
		return new VerticalBlockSample(0, sample);
	}

}
