package net.ludocrypt.corners.world.chunk;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

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
import net.minecraft.loot.LootTables;
import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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

public class CommunalCorridorsChunkGenerator extends ChunkGeneratorExtraInfo {

	public final HashMap<String, NbtPlacerUtil> structures = new HashMap<String, NbtPlacerUtil>(30);

	public static final Codec<CommunalCorridorsChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.worldSeed;
		})).apply(instance, instance.stable(CommunalCorridorsChunkGenerator::new));
	});

	public final long worldSeed;

	public CommunalCorridorsChunkGenerator(BiomeSource biomeSource, long worldSeed) {
		super(biomeSource, biomeSource, new StructuresConfig(false), worldSeed);
		this.worldSeed = worldSeed;
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
				Random random = new Random(region.getSeed() * Math.floorDiv(chunk.getPos().getStartX(), 8) * Math.floorDiv(chunk.getPos().getStartZ(), 8) + x + z);
				if (random.nextDouble() < 0.75) {
					generateNbt(region, chunkPos.getStartPos().add(x * 8, 0, z * 8), "communal_corridors_" + (random.nextInt(20) + 1));
				} else {
					generateNbt(region, chunkPos.getStartPos().add(x * 8, 0, z * 8), "communal_corridors_decorated_" + (random.nextInt(8) + 1));
				}
			}
		}

		return CompletableFuture.completedFuture(chunk);
	}

	private void store(String id, ServerWorld world) {
		structures.put(id, NbtPlacerUtil.load(world.getServer().getResourceManager(), TheCorners.id("nbt/communal_corridors/" + id + ".nbt")).get());
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
		structures.get(id).rotate(rotation).generateNbt(region, at, (pos, state) -> {
			if (!state.isAir()) {
				if (state.isOf(Blocks.BARREL)) {
					if (region.getRandom().nextDouble() < 0.45D) {
						region.setBlockState(pos, state, Block.NOTIFY_ALL, 1);
						if (region.getBlockEntity(pos)instanceof BarrelBlockEntity barrel) {
							barrel.setLootTable(LootTables.SIMPLE_DUNGEON_CHEST, region.getSeed() ^ pos.getX() ^ pos.getZ() ^ pos.getY());
						}
					}
				} else if (state.isOf(CornerBlocks.DEBUG_AIR_BLOCK)) {
					region.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL, 1);
				} else {
					if (state.getBlock()instanceof DebugPaintingSpawnerBlock paintingSpawner) {
						region.spawnEntity(DimensionalPaintingEntity.createFromMotive(region.toServerWorld(), pos, state.get(DebugPaintingSpawnerBlock.FACING), paintingSpawner.motive));
					} else {
						region.setBlockState(pos, state, Block.NOTIFY_ALL, 1);
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
