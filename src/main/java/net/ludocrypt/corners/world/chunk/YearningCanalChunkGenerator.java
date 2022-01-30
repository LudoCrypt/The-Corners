package net.ludocrypt.corners.world.chunk;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.config.CornerConfig;
import net.ludocrypt.limlib.api.world.FlatMultiNoiseSampler;
import net.ludocrypt.limlib.api.world.NbtChunkGenerator;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class YearningCanalChunkGenerator extends NbtChunkGenerator {

	public static final Codec<YearningCanalChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.worldSeed;
		})).apply(instance, instance.stable(YearningCanalChunkGenerator::new));
	});

	public YearningCanalChunkGenerator(BiomeSource biomeSource, long worldSeed) {
		super(biomeSource, new FlatMultiNoiseSampler(1.0F), worldSeed, TheCorners.id("yearning_canal"));
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return new YearningCanalChunkGenerator(this.biomeSource, seed);
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, Chunk chunk, ChunkStatus targetStatus, ServerWorld world, ChunkRegion region, StructureManager structureManager, ServerLightingProvider lightingProvider) {
		ChunkPos chunkPos = chunk.getPos();
		int max = Math.floorDiv(chunk.getTopY(), 54);
		Random random = new Random(region.getSeed() + Math.floorDiv(chunkPos.getStartX(), 19) + Math.floorDiv(chunkPos.getStartZ(), 19) + 1);
		for (int yi = 0; yi < max; yi++) {
			BlockPos pos = chunkPos.getStartPos().add(0, yi * 54, 0);

			Random yRandom = new Random(region.getSeed() + MathHelper.hashCode(yi * 2, yi * 3, yi));
			boolean hallwaySpawnsAtHeight = (yRandom.nextDouble() < 0.875D && yRandom.nextBoolean()) && (yi != 0 && yi != max - 1);
			Direction dir = Direction.fromHorizontal(yRandom.nextInt(4));
			BlockRotation rotation = dir.equals(Direction.NORTH) ? BlockRotation.COUNTERCLOCKWISE_90 : dir.equals(Direction.EAST) ? BlockRotation.NONE : dir.equals(Direction.SOUTH) ? BlockRotation.CLOCKWISE_90 : BlockRotation.CLOCKWISE_180;
			BlockPos offsetPos = pos.add((dir.equals(Direction.NORTH) ? 6 : dir.equals(Direction.EAST) ? 12 : dir.equals(Direction.SOUTH) ? 6 : -10), (dir.equals(Direction.NORTH) ? 13 : dir.equals(Direction.EAST) ? 23 : dir.equals(Direction.SOUTH) ? 22 : 15), (dir.equals(Direction.NORTH) ? -10 : dir.equals(Direction.EAST) ? 6 : dir.equals(Direction.SOUTH) ? 12 : 6));
			if (pos.getX() == 0 && pos.getZ() == 0) {
				if (yi == 0) {
					generateNbt(region, pos, "yearning_canal_bottom");
					continue;
				} else if (yi == max - 1) {
					generateNbt(region, pos, "yearning_canal_top");
					continue;
				} else {
					if (hallwaySpawnsAtHeight) {
						generateNbt(region, pos, "yearning_canal_4");
						generateNbt(region, offsetPos, "yearning_canal_hallway_connected", rotation);
					} else {
						generateNbt(region, pos, "yearning_canal_" + (random.nextInt(15) + 1));
					}
				}
			}
			if (hallwaySpawnsAtHeight) {
				if ((dir.equals(Direction.NORTH) && chunkPos.x == 0 && chunkPos.z <= 0) || (dir.equals(Direction.WEST) && chunkPos.z == 0 && chunkPos.x <= 0) || (dir.equals(Direction.SOUTH) && chunkPos.x == 0 && chunkPos.z >= 1) || (dir.equals(Direction.EAST) && chunkPos.z == 0 && chunkPos.x >= 1)) {
					Random hallRandom = new Random(region.getSeed() + MathHelper.hashCode(chunkPos.getStartX(), chunkPos.getStartZ(), 2));
					generateNbt(region, offsetPos.add(0, 4, 0), "yearning_canal_hallway_" + (hallRandom.nextInt(3) == 0 ? (hallRandom.nextInt(13) + 1) : 1), rotation);
				}
			}
			continue;

		}

		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public void storeStructures(ServerWorld world) {
		store("yearning_canal", world, 1, 15);
		store("yearning_canal_bottom", world);
		store("yearning_canal_top", world);
		store("yearning_canal_hallway", world, 1, 13);
		store("yearning_canal_hallway_connected", world);
	}

	@Override
	public int getChunkRadius() {
		return 1;
	}

	@Override
	public int getWorldHeight() {
		return CornerConfig.getInstance().condensedDimensions ? 432 : 2032;
	}

	@Override
	public int getHeight(int x, int y, Heightmap.Type type, HeightLimitView world) {
		return world.getTopY();
	}

}
