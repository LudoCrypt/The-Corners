package net.ludocrypt.corners.world.chunk;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import org.apache.logging.log4j.util.TriConsumer;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.block.DebugPaintingSpawnerBlock;
import net.ludocrypt.corners.entity.DimensionalPaintingEntity;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.util.NbtPlacerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class YearningCanalChunkGenerator extends ChunkGeneratorExtraInfo {

	public final HashMap<String, NbtPlacerUtil> structures = new HashMap<String, NbtPlacerUtil>(30);

	public static final Codec<YearningCanalChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.worldSeed;
		})).apply(instance, instance.stable(YearningCanalChunkGenerator::new));
	});

	public final long worldSeed;

	public YearningCanalChunkGenerator(BiomeSource biomeSource, long worldSeed) {
		super(biomeSource, biomeSource, new StructuresConfig(false), worldSeed);
		this.worldSeed = worldSeed;
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
			store("yearning_canal_1", world);
			store("yearning_canal_2", world);
			store("yearning_canal_3", world);
			store("yearning_canal_4", world);
			store("yearning_canal_5", world);
			store("yearning_canal_6", world);
			store("yearning_canal_7", world);
			store("yearning_canal_8", world);
			store("yearning_canal_9", world);
			store("yearning_canal_10", world);
			store("yearning_canal_11", world);
			store("yearning_canal_12", world);
			store("yearning_canal_13", world);
			store("yearning_canal_14", world);
			store("yearning_canal_15", world);
			store("yearning_canal_bottom", world);
			store("yearning_canal_top", world);
			store("yearning_canal_hallway_1", world);
			store("yearning_canal_hallway_2", world);
			store("yearning_canal_hallway_3", world);
			store("yearning_canal_hallway_4", world);
			store("yearning_canal_hallway_5", world);
			store("yearning_canal_hallway_6", world);
			store("yearning_canal_hallway_7", world);
			store("yearning_canal_hallway_8", world);
			store("yearning_canal_hallway_9", world);
			store("yearning_canal_hallway_10", world);
			store("yearning_canal_hallway_11", world);
			store("yearning_canal_hallway_12", world);
			store("yearning_canal_hallway_connected", world);
		}

		ChunkPos chunkPos = chunk.getPos();
		int max = Math.floorDiv(chunk.getTopY(), 54);
		Random random = new Random(region.getSeed() + Math.floorDiv(chunkPos.getStartX(), 19) + Math.floorDiv(chunkPos.getStartZ(), 19) + 1);
		for (int yi = 0; yi < max; yi++) {
			BlockPos pos = chunkPos.getStartPos().add(0, yi * 54, 0);

			Random yRandom = new Random(region.getSeed() * yi + 2);
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
					Random hallRandom = new Random(region.getSeed() + Math.floorDiv(chunkPos.getStartX(), 16) + Math.floorDiv(chunkPos.getStartZ(), 16) + 2);
					generateNbt(region, offsetPos.add(0, 4, 0), "yearning_canal_hallway_" + (hallRandom.nextInt(3) == 0 ? (hallRandom.nextInt(12) + 1) : 1), rotation);
				}
			}
			continue;

		}

		return CompletableFuture.completedFuture(chunk);
	}

	private void store(String id, ServerWorld world) {
		structures.put(id, NbtPlacerUtil.load(world.getServer().getResourceManager(), TheCorners.id("nbt/yearning_canal/" + id + ".nbt")).get());
	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world) {
		return world.getTopY();
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		return new VerticalBlockSample(0, new BlockState[world.getHeight()]);
	}

	public void generateNbt(ChunkRegion region, BlockPos at, String id) {
		generateNbt(region, at, id, BlockRotation.NONE);
	}

	public void generateNbt(ChunkRegion region, BlockPos at, String id, BlockRotation rotation) {

		TriConsumer<BlockPos, BlockState, NbtCompound> checkForBlockEntity = (p, s, n) -> {
			BlockEntity blockEntity = region.getBlockEntity(p);
			if (blockEntity != null) {
				if (s.isOf(blockEntity.getCachedState().getBlock())) {
					blockEntity.writeNbt(n);
				}
			}
		};

		structures.get(id).rotate(rotation).generateNbt(region, at, (pos, state, nbt) -> {
			if (!state.isAir()) {
				if (state.isOf(Blocks.BARREL)) {
					if (region.getRandom().nextDouble() < 0.45D) {
						region.setBlockState(pos, state, Block.NOTIFY_ALL, 1);
						checkForBlockEntity.accept(pos, state, nbt);
						if (region.getBlockEntity(pos)instanceof BarrelBlockEntity barrel) {
							barrel.setLootTable(LootTables.SIMPLE_DUNGEON_CHEST, region.getSeed() ^ pos.getX() ^ pos.getZ() ^ pos.getY());
						}
					}
				} else if (state.isOf(CornerBlocks.DEBUG_AIR_BLOCK)) {
					region.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL, 1);
					checkForBlockEntity.accept(pos, state, nbt);
				} else {
					if (state.getBlock()instanceof DebugPaintingSpawnerBlock paintingSpawner) {
						region.spawnEntity(DimensionalPaintingEntity.createFromMotive(region.toServerWorld(), pos, state.get(DebugPaintingSpawnerBlock.FACING), paintingSpawner.motive));
					} else {
						region.setBlockState(pos, state, Block.NOTIFY_ALL, 1);
						checkForBlockEntity.accept(pos, state, nbt);
					}
				}
			}
		});
	}

	@Override
	public int getPlacementRadius() {
		return 1;
	}

}
