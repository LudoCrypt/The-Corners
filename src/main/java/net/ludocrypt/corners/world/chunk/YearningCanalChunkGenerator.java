package net.ludocrypt.corners.world.chunk;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class YearningCanalChunkGenerator extends LiminalChunkGenerator {

	public static final Codec<YearningCanalChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.worldSeed;
		})).apply(instance, instance.stable(YearningCanalChunkGenerator::new));
	});

	public YearningCanalChunkGenerator(BiomeSource biomeSource, long worldSeed) {
		super(biomeSource, worldSeed, "yearning_canal");
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
	public void buildSurface(ChunkRegion region, Chunk chunk) {

	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk, ChunkStatus targetStatus, ServerWorld world, ChunkRegion region, ChunkGenerator chunkGenerator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, Unloaded>>> function, List<Chunk> list) {
		if (structures.isEmpty()) {
			store("yearning_canal", world, 1, 15);
			store("yearning_canal_bottom", world);
			store("yearning_canal_top", world);
			store("yearning_canal_hallway", world, 1, 13);
			store("yearning_canal_hallway_connected", world);
		}

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
	protected void generateNbt(ChunkRegion region, BlockPos at, String id, BlockRotation rotation) {
		structures.get(id).rotate(rotation).generateNbt(region, at, (pos, state, nbt) -> {
			if (!state.isAir()) {
				if (state.isOf(Blocks.BARREL)) {
					if (region.getRandom().nextDouble() < 0.45D) {
						region.setBlockState(pos, state, Block.NOTIFY_ALL, 1);
						if (region.getBlockEntity(pos)instanceof BarrelBlockEntity barrel) {
							barrel.setLootTable(LootTables.SIMPLE_DUNGEON_CHEST, region.getSeed() + MathHelper.hashCode(pos));
						}
					}
				} else if (state.isOf(Blocks.BARRIER)) {
					region.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL, 1);
				} else {
					region.setBlockState(pos, state, Block.NOTIFY_ALL, 1);
				}
				BlockEntity blockEntity = region.getBlockEntity(pos);
				if (blockEntity != null) {
					if (state.isOf(blockEntity.getCachedState().getBlock())) {
						blockEntity.writeNbt(nbt);
					}
				}
			}
		}).spawnEntities(region, at, rotation);
	}

	@Override
	public int getPlacementRadius() {
		return 1;
	}

}
