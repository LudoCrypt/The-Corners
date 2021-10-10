package net.ludocrypt.corners.world.chunk;

import java.util.HashMap;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.util.NbtPlacerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public abstract class LiminalChunkGenerator extends ChunkGeneratorExtraInfo {

	public final HashMap<String, NbtPlacerUtil> structures = new HashMap<String, NbtPlacerUtil>(30);

	public final long worldSeed;
	public final String nbtId;

	public LiminalChunkGenerator(BiomeSource biomeSource, long worldSeed, String nbtId) {
		super(biomeSource, biomeSource, new StructuresConfig(false), worldSeed);
		this.worldSeed = worldSeed;
		this.nbtId = nbtId;
	}

	protected void store(String id, ServerWorld world) {
		structures.put(id, NbtPlacerUtil.load(world.getServer().getResourceManager(), TheCorners.id("nbt/" + this.nbtId + "/" + id + ".nbt")).get());
	}

	protected void store(String id, ServerWorld world, int from, int to) {
		for (int i = from; i <= to; i++) {
			store(id + "_" + i, world);
		}
	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world) {
		return world.getTopY();
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		return new VerticalBlockSample(0, new BlockState[world.getHeight()]);
	}

	protected void generateNbt(ChunkRegion region, BlockPos at, String id) {
		generateNbt(region, at, id, BlockRotation.NONE);
	}

	protected void generateNbt(ChunkRegion region, BlockPos at, String id, BlockRotation rotation) {
		structures.get(id).rotate(rotation).generateNbt(region, at, (pos, state, nbt) -> {
			if (!state.isAir()) {
				if (state.isOf(Blocks.BARREL)) {
					region.setBlockState(pos, state, Block.NOTIFY_ALL, 1);
					if (region.getBlockEntity(pos)instanceof BarrelBlockEntity barrel) {
						barrel.setLootTable(LootTables.SIMPLE_DUNGEON_CHEST, region.getSeed() + MathHelper.hashCode(pos));
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

}
