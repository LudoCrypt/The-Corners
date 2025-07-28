package net.ludocrypt.limlib.api.world.chunk;

import java.util.Optional;

import org.apache.logging.log4j.util.TriConsumer;

import net.ludocrypt.limlib.api.world.LimlibHelper;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.nbt.FunctionMap;
import net.ludocrypt.limlib.api.world.nbt.NbtGroup;
import net.ludocrypt.limlib.api.world.nbt.NbtPlacerUtil;
import net.ludocrypt.limlib.api.world.nbt.NbtPlacerUtil.Bound;
import net.ludocrypt.limlib.api.world.nbt.NbtTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;

public abstract class AbstractNbtChunkGenerator extends LiminalChunkGenerator {

	public final NbtGroup nbtGroup;
	public final FunctionMap<Identifier, NbtPlacerUtil, ResourceManager> structures;
	public NbtTags tags;

	public AbstractNbtChunkGenerator(BiomeSource biomeSource, NbtGroup nbtGroup) {
		this(biomeSource, nbtGroup, new FunctionMap<Identifier, NbtPlacerUtil, ResourceManager>(NbtPlacerUtil::load));
	}

	public AbstractNbtChunkGenerator(BiomeSource biomeSource, NbtGroup nbtGroup,
			FunctionMap<Identifier, NbtPlacerUtil, ResourceManager> structures) {
		super(biomeSource);
		this.nbtGroup = nbtGroup;
		this.structures = structures;
		this.nbtGroup.fill(structures);
	}

	public abstract void loadTags();

	public void generateNbt(ChunkRegion region, BlockPos at, Identifier id) {
		generateNbt(region, at, id, Manipulation.NONE);
	}

	public void generateNbt(ChunkRegion region, BlockPos at, Identifier id, Manipulation manipulation) {

		try {
			structures
				.eval(id, region.getServer().getResourceManager())
				.manipulate(manipulation)
				.generateNbt(region, at, (pos, state, nbt) -> this.modifyStructure(region, pos, state, nbt))
				.spawnEntities(region, at, manipulation, (nbt) -> this.modifyEntity(region, nbt));
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new NullPointerException("Attempted to load undefined structure \'" + id + "\'");
		}

	}

	public void generateNbt(ChunkRegion region, BlockPos at, Identifier id, Manipulation manipulation,
			TriConsumer<BlockPos, BlockState, Optional<NbtCompound>> consumer) {

		try {
			structures
				.eval(id, region.getServer().getResourceManager())
				.manipulate(manipulation)
				.generateNbt(region, at, consumer)
				.spawnEntities(region, at, manipulation, (nbt) -> this.modifyEntity(region, nbt));
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new NullPointerException("Attempted to load undefined structure \'" + id + "\'");
		}

	}

	public void generateNbt(ChunkRegion region, BlockPos offset, BlockPos from, Bound to, Identifier id) {
		generateNbt(region, offset, from, to, id, Manipulation.NONE);
	}

	public void generateNbt(ChunkRegion region, BlockPos offset, BlockPos from, Bound to, Identifier id,
			Manipulation manipulation) {

		try {
			structures
				.eval(id, region.getServer().getResourceManager())
				.manipulate(manipulation)
				.generateNbt(region, offset, from, to, (pos, state, nbt) -> this.modifyStructure(region, pos, state, nbt))
				.spawnEntities(region, offset, from, to, manipulation, (nbt) -> this.modifyEntity(region, nbt));
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new NullPointerException("Attempted to load undefined structure \'" + id + "\'");
		}

	}

	public void generateNbt(ChunkRegion region, BlockPos offset, BlockPos from, Bound to, Identifier id,
			Manipulation manipulation, TriConsumer<BlockPos, BlockState, Optional<NbtCompound>> consumer) {

		try {
			structures
				.eval(id, region.getServer().getResourceManager())
				.manipulate(manipulation)
				.generateNbt(region, offset, from, to, consumer)
				.spawnEntities(region, offset, from, to, manipulation, (nbt) -> this.modifyEntity(region, nbt));
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new NullPointerException("Attempted to load undefined structure \'" + id + "\'");
		}

	}

	protected void modifyStructure(ChunkRegion region, BlockPos pos, BlockState state,
			Optional<NbtCompound> blockEntityNbt) {
		this.modifyStructure(region, pos, state, blockEntityNbt, Block.NOTIFY_ALL, 1);
	}

	protected void modifyStructure(ChunkRegion region, BlockPos pos, BlockState state, Optional<NbtCompound> blockEntityNbt,
			int update, int depth) {

		if (state.isOf(Blocks.STRUCTURE_VOID)) {
			return;
		}

		region.setBlockState(pos, state, update, depth);

		if (blockEntityNbt.isPresent()) {
			BlockEntity blockEntity = region.getBlockEntity(pos);

			if (blockEntity != null) {

				if (state.isOf(blockEntity.getCachedState().getBlock())) {
					blockEntity.readNbt(blockEntityNbt.get());
				}

			}

			if (blockEntity instanceof LootableContainerBlockEntity lootTable) {
				lootTable
					.setLootTable(this.getContainerLootTable(lootTable), region.getSeed() + LimlibHelper.blockSeed(pos));
			}

		}

	}

	protected Identifier getContainerLootTable(LootableContainerBlockEntity container) {
		return LootTables.SIMPLE_DUNGEON_CHEST;
	}

	protected NbtCompound modifyEntity(ChunkRegion region, NbtCompound nbt) {
		return nbt;
	}

}
