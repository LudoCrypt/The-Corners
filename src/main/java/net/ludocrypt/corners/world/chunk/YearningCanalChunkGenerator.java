package net.ludocrypt.corners.world.chunk;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.util.NbtPlacerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
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
	public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk, ChunkStatus targetStatus, ServerWorld world, ChunkGenerator chunkGenerator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, Unloaded>>> function, List<Chunk> list) {
		return CompletableFuture.supplyAsync(() -> {

			ChunkRegion region = new ChunkRegion(world, list, targetStatus, 0);
			ResourceManager resourceManager = world.getServer().getResourceManager();
			Optional<NbtPlacerUtil> optional = NbtPlacerUtil.load(resourceManager, TheCorners.id("nbt/yearning_canal/yearning_canal_bottom.nbt"));
			Optional<NbtPlacerUtil> optionalHallway = NbtPlacerUtil.load(resourceManager, TheCorners.id("nbt/yearning_canal/yearning_canal_hallway_1.nbt"));
			if (optional.isPresent() && optionalHallway.isPresent()) {
				NbtPlacerUtil nbt = optional.get();
				NbtPlacerUtil hallwayNbt = optionalHallway.get();
				int countUp = Math.floorDiv(chunk.getTopY(), nbt.sizeY);
				Random random = new Random(region.getSeed() ^ Math.floorDiv(chunk.getPos().getStartX(), nbt.sizeX) ^ Math.floorDiv(chunk.getPos().getStartZ(), nbt.sizeZ));
				for (int i = 0; i < countUp; i++) {
					int y = i * nbt.sizeY;
					if (i == 0) {
						generateNbt(region, chunk, "yearning_canal_bottom", y);
						continue;
					} else if (i == countUp - 1) {
						generateNbt(region, chunk, "yearning_canal_top", y);
						continue;
					}
					Random hallwayRandom = new Random(region.getSeed() ^ y ^ -2);
					Direction dir = shouldGenerateHallwayHere(region, chunk, hallwayRandom, y);
					if (dir != null) {
						generateNbt(region, chunk, "yearning_canal_4", y);
						BlockRotation rotation = dir.equals(Direction.NORTH) ? BlockRotation.COUNTERCLOCKWISE_90 : dir.equals(Direction.EAST) ? BlockRotation.NONE : dir.equals(Direction.SOUTH) ? BlockRotation.CLOCKWISE_90 : BlockRotation.CLOCKWISE_180;
						BlockPos offsetPos = new BlockPos((dir.equals(Direction.NORTH) ? 6 : dir.equals(Direction.EAST) ? 12 : dir.equals(Direction.SOUTH) ? 6 : -10), y + (dir.equals(Direction.NORTH) ? 13 : dir.equals(Direction.EAST) ? 23 : dir.equals(Direction.SOUTH) ? 22 : 15), (dir.equals(Direction.NORTH) ? -10 : dir.equals(Direction.EAST) ? 6 : dir.equals(Direction.SOUTH) ? 12 : 6));
						generateNbt(region, chunk, "yearning_canal_hallway_connected", offsetPos.getX(), offsetPos.getZ(), offsetPos.getY(), rotation, true);

						if ((dir.equals(Direction.NORTH) && chunk.getPos().getStartZ() <= -16) || (dir.equals(Direction.EAST) && chunk.getPos().getStartX() >= 16) || (dir.equals(Direction.SOUTH) && chunk.getPos().getStartZ() >= 16) || (dir.equals(Direction.WEST) && chunk.getPos().getStartX() <= -16)) {
							getOffsets(chunk.getPos().getStartPos().getComponentAlongAxis(dir.getAxis()), hallwayNbt.sizeX, -offsetPos.getComponentAlongAxis(dir.getAxis())).forEach((zint) -> {
								if ((dir.equals(Direction.NORTH) && zint <= -16) || (dir.equals(Direction.EAST) && zint >= 16) || (dir.equals(Direction.SOUTH) && zint >= 16) || (dir.equals(Direction.WEST) && zint <= -16)) {
									Random rand = new Random(region.getSeed() ^ y ^ zint ^ dir.getHorizontal());
									generateNbt(region, chunk, "yearning_canal_hallway_" + (rand.nextBoolean() ? (rand.nextInt(12) + 1) : 1), dir.getAxis().equals(Direction.Axis.X) ? zint : offsetPos.getX(), dir.getAxis().equals(Direction.Axis.Z) ? zint : offsetPos.getZ(), offsetPos.getY() + 4, rotation, true);
								}
							});
						}
						continue;
					} else {
						generateNbt(region, chunk, "yearning_canal_" + (random.nextInt(15) + 1), y);
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
		return new VerticalBlockSample(0, new BlockState[world.getHeight()]);
	}

	public Direction shouldGenerateHallwayHere(ChunkRegion region, Chunk chunk, Random random, int y) {
		if (random.nextDouble() < 0.875D && random.nextBoolean()) {
			return Direction.fromHorizontal(random.nextInt(4));
		}
		return null;
	}

	public void generateNbt(ChunkRegion region, Chunk chunk, String id, int y) {
		generateNbt(region, chunk, id, 0, 0, y, BlockRotation.NONE, true);
	}

	public void generateNbt(ChunkRegion region, Chunk chunk, String id, int x, int z, int y, BlockRotation rotation, boolean ignoreAir) {
		NbtPlacerUtil.generateNbt(region, chunk, TheCorners.id("nbt/yearning_canal/" + id + ".nbt"), new BlockPos(x, y, z), (pos, state) -> {
			if ((ignoreAir && (!state.isAir()))) {
				if (state.isOf(Blocks.BARREL)) {
					if (region.getRandom().nextDouble() < 0.45D) {
						region.setBlockState(pos, state, Block.NOTIFY_ALL, 1);
						if (region.getBlockEntity(pos)instanceof BarrelBlockEntity barrel) {
							barrel.setLootTable(LootTables.SIMPLE_DUNGEON_CHEST, region.getSeed());
						}
					}
				} else if (state.isOf(Blocks.JIGSAW)) {
					region.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL, 1);
				} else {
					region.setBlockState(pos, state, Block.NOTIFY_ALL, 1);
				}
			}
		}, rotation);
	}

	public List<Integer> getOffsets(int start, int size, int offset) {
		List<Integer> hcl = Lists.newArrayList();
		for (int hi = 0; hi < 16; hi++) {
			int hp = start + hi;
			if (((hp + offset) % size == 0)) {
				if ((hcl.size() == 0) && hi != 0) {
					hcl.add(hp - size);
				}
				hcl.add(hp);
			}
		}
		return hcl;
	}

}
