package net.ludocrypt.corners.world.chunk;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.world.maze.HoaryCrossroadsMazeGenerator;
import net.ludocrypt.limlib.api.world.AbstractNbtChunkGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class HoaryCrossroadsChunkGenerator extends AbstractNbtChunkGenerator {

	public static final Codec<HoaryCrossroadsChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), HoaryCrossroadsMazeGenerator.CODEC.fieldOf("maze_generator").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeGenerator;
		})).apply(instance, instance.stable(HoaryCrossroadsChunkGenerator::new));
	});

	private HoaryCrossroadsMazeGenerator mazeGenerator;

	public HoaryCrossroadsChunkGenerator(BiomeSource biomeSource, HoaryCrossroadsMazeGenerator mazeGenerator) {
		super(new SimpleRegistry<StructureSet>(Registry.STRUCTURE_SET_KEY, Lifecycle.stable(), null), Optional.empty(), biomeSource, TheCorners.id("hoary_crossroads"));
		this.mazeGenerator = mazeGenerator;
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator, StructureTemplateManager structureTemplateManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, Unloaded>>> fullChunkConverter, List<Chunk> chunks, Chunk chunk, boolean regenerate) {
		BlockPos startPos = chunk.getPos().getStartPos();
		this.mazeGenerator.generateMaze(startPos, chunk, region, this);

		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public void storeStructures(ServerWorld world) {
		store("hoary_crossroads_f", world, 0, 7);
		store("hoary_crossroads_i", world, 0, 9);
		store("hoary_crossroads_l", world, 0, 9);
		store("hoary_crossroads_t", world, 0, 7);
		store("hoary_crossroads_nub", world, 0, 7);
		store("hoary_crossroads_f_rare_0", world);
		store("hoary_crossroads_i_rare_0", world);
		store("hoary_crossroads_l_rare_0", world);
		store("hoary_crossroads_t_rare_0", world);
		store("hoary_crossroads_t_rare_1", world);
		store("hoary_crossroads_f_bottom", world);
		store("hoary_crossroads_i_bottom", world);
		store("hoary_crossroads_l_bottom", world);
		store("hoary_crossroads_t_bottom", world);
		store("hoary_crossroads_nub_bottom", world);
		store("hoary_crossroads_obelisk", world, 0, 4);
	}

	@Override
	public int chunkRadius() {
		return 1;
	}

	@Override
	protected Identifier getBarrelLootTable() {
		return LootTables.SHIPWRECK_SUPPLY_CHEST;
	}

	@Override
	protected void modifyStructure(ChunkRegion region, BlockPos pos, BlockState state, NbtCompound nbt) {
		super.modifyStructure(region, pos, state, nbt);
		if (state.isOf(Blocks.CHEST)) {
			region.setBlockState(pos, state, Block.NOTIFY_ALL, 1);
			if (region.getBlockEntity(pos)instanceof LootableContainerBlockEntity lootTable) {
				lootTable.setLootTable(this.getBarrelLootTable(), region.getSeed() + MathHelper.hashCode(pos));
			}
		}
	}

	@Override
	public int getWorldHeight() {
		return 512;
	}

	@Override
	public int getSeaLevel() {
		return 0;
	}

	@Override
	public int getMinimumY() {
		return 0;
	}

}
