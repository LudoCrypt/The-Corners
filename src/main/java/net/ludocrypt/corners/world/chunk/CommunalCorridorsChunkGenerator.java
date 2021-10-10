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
			store("communal_corridors", world, 1, 5);
			store("communal_corridors_decorated", world, 1, 22);
			store("communal_corridors_decorated_big", world, 1, 3);
		}

		ChunkPos chunkPos = chunk.getPos();
		Random fullChunkRandom = new Random(region.getSeed() + MathHelper.hashCode(chunk.getPos().getStartX(), chunk.getPos().getStartZ(), -69420));

		if (!(fullChunkRandom.nextDouble() < 0.31275D && fullChunkRandom.nextInt(8) == 0)) {
			for (int x = 0; x < 2; x++) {
				for (int z = 0; z < 2; z++) {
					Random random = new Random(region.getSeed() + MathHelper.hashCode(chunk.getPos().getStartX(), chunk.getPos().getStartZ(), x + z));
					if (random.nextDouble() < 0.2375625D) {
						generateNbt(region, chunkPos.getStartPos().add(x * 8, 1, z * 8), "communal_corridors_" + (random.nextInt(5) + 1));
					} else {
						generateNbt(region, chunkPos.getStartPos().add(x * 8, 1, z * 8), "communal_corridors_decorated_" + (random.nextInt(22) + 1));
					}
				}
			}
		} else {
			generateNbt(region, chunkPos.getStartPos().add(0, 1, 0), "communal_corridors_decorated_big_" + (fullChunkRandom.nextInt(3) + 1));
		}

		for (int x = chunk.getPos().getStartX(); x < chunk.getPos().getStartX() + 16; x++) {
			for (int z = chunk.getPos().getStartZ(); z < chunk.getPos().getStartZ() + 16; z++) {
				region.setBlockState(new BlockPos(x, 0, z), Blocks.OAK_PLANKS.getDefaultState(), Block.FORCE_STATE, 0);
			}
		}

		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	protected void generateNbt(ChunkRegion region, BlockPos at, String id, BlockRotation rotation) {
		structures.get(id).rotate(rotation).generateNbt(region, at, (pos, state, nbt) -> {
			if (!state.isAir()) {
				if (state.isOf(Blocks.BARREL)) {
					region.setBlockState(pos, state, Block.NOTIFY_ALL, 1);
					if (region.getBlockEntity(pos)instanceof BarrelBlockEntity barrel) {
						barrel.setLootTable(LootTables.SPAWN_BONUS_CHEST, region.getSeed() + MathHelper.hashCode(pos));
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