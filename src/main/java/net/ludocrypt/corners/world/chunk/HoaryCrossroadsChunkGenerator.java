package net.ludocrypt.corners.world.chunk;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.init.CornerWorlds;
import net.ludocrypt.corners.world.maze.HoaryCrossroadsMazeGenerator;
import net.ludocrypt.limlib.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.world.maze.DepthFirstMaze;
import net.ludocrypt.limlib.world.maze.DepthFirstMazeSolver;
import net.ludocrypt.limlib.world.maze.DilateMaze;
import net.ludocrypt.limlib.world.maze.MazeComponent;
import net.ludocrypt.limlib.world.maze.MazeComponent.CellState;
import net.ludocrypt.limlib.world.maze.MazeComponent.Vec2i;
import net.ludocrypt.limlib.world.maze.RectangularMazePiece;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.StructureSet;

public class HoaryCrossroadsChunkGenerator extends AbstractNbtChunkGenerator {

	public static final Codec<HoaryCrossroadsChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.populationSource;
		}), Codec.INT.fieldOf("maze_width").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeWidth;
		}), Codec.INT.fieldOf("maze_height").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeHeight;
		}), Codec.INT.fieldOf("maze_dilation").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeDilation;
		}), Codec.LONG.fieldOf("seed_modifier").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeSeedModifier;
		})).apply(instance, instance.stable(HoaryCrossroadsChunkGenerator::new));
	});

	private HoaryCrossroadsMazeGenerator mazeGenerator;
	private int mazeWidth;
	private int mazeHeight;
	private int mazeDilation;
	private long mazeSeedModifier;

	public HoaryCrossroadsChunkGenerator(BiomeSource biomeSource, int mazeWidth, int mazeHeight, int mazeDilation, long mazeSeedModifier) {
		super(new SimpleRegistry<StructureSet>(Registry.STRUCTURE_SET_WORLDGEN, Lifecycle.stable(), null), Optional.empty(), biomeSource, TheCorners.id(CornerWorlds.HOARY_CROSSROADS));
		this.mazeWidth = mazeWidth;
		this.mazeHeight = mazeHeight;
		this.mazeDilation = mazeDilation;
		this.mazeSeedModifier = mazeSeedModifier;
		this.mazeGenerator = new HoaryCrossroadsMazeGenerator(this.mazeWidth, this.mazeHeight, this.mazeDilation, this.mazeSeedModifier);
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator, StructureTemplateManager structureTemplateManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, Unloaded>>> fullChunkConverter, List<Chunk> chunks, Chunk chunk, boolean regenerate) {
		BlockPos startPos = chunk.getPos().getStartPos();
		this.mazeGenerator.generateMaze(startPos, region.getSeed(), this::newMaze, (pos, mazePos, maze, cellState, thickness) -> decorateCell(pos, mazePos, maze, cellState, thickness, region));

		return CompletableFuture.completedFuture(chunk);
	}

	public MazeComponent newMaze(BlockPos mazePos, int width, int height, RandomGenerator random) {
		BlockPos grandMazePos = new BlockPos(mazePos.getX() - mazeGenerator.mod(mazePos.getX(), (mazeGenerator.width * mazeGenerator.width * mazeGenerator.thickness)), 0, mazePos.getZ() - mazeGenerator.mod(mazePos.getZ(), (mazeGenerator.height * mazeGenerator.height * mazeGenerator.thickness)));

		MazeComponent grandMaze;
		if (mazeGenerator.grandMazeMap.containsKey(grandMazePos)) {
			grandMaze = mazeGenerator.grandMazeMap.get(grandMazePos);
		} else {
			grandMaze = new DepthFirstMaze(mazeGenerator.width / mazeGenerator.dilation, mazeGenerator.height / mazeGenerator.dilation, RandomGenerator.createLegacy(mazeGenerator.blockSeed(grandMazePos.getX(), mazeGenerator.seedModifier, grandMazePos.getZ())));
			grandMaze.generateMaze();
			mazeGenerator.grandMazeMap.put(grandMazePos, grandMaze);
		}

		CellState originCell = grandMaze.cellState((((mazePos.getX() - grandMazePos.getX()) / mazeGenerator.thickness) / mazeGenerator.width) / mazeGenerator.dilation, (((mazePos.getZ() - grandMazePos.getZ()) / mazeGenerator.thickness) / height) / mazeGenerator.dilation);

		Vec2i start = null;
		List<Vec2i> endings = Lists.newArrayList();

		if (originCell.isSouth() || originCell.getPosition().getX() == 0) {
			if (start == null) {
				start = new Vec2i(0, (mazeGenerator.height / mazeGenerator.dilation) / 2);
			}
		}

		if (originCell.isWest() || originCell.getPosition().getY() == 0) {
			if (start == null) {
				start = new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2, 0);
			} else {
				endings.add(new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2, 0));
			}
		}

		if (originCell.isNorth() || originCell.getPosition().getX() == (mazeGenerator.width / mazeGenerator.dilation) - 1) {
			if (start == null) {
				start = new Vec2i((mazeGenerator.width / mazeGenerator.dilation) - 1, (mazeGenerator.height / mazeGenerator.dilation) / 2);
			} else {
				endings.add(new Vec2i((mazeGenerator.width / mazeGenerator.dilation) - 1, (mazeGenerator.height / mazeGenerator.dilation) / 2));
			}
		}

		if (originCell.isEast() || originCell.getPosition().getY() == (mazeGenerator.height / mazeGenerator.dilation) - 1) {
			if (start == null) {
				start = new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2, (mazeGenerator.height / mazeGenerator.dilation) - 1);
			} else {
				endings.add(new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2, (mazeGenerator.height / mazeGenerator.dilation) - 1));
			}
		}

		// Allow Grand Nubs
		if (endings.isEmpty()) {
			endings.add(new Vec2i(random.nextInt((mazeGenerator.width / mazeGenerator.dilation) - 2) + 1, random.nextInt((mazeGenerator.height / mazeGenerator.dilation) - 2) + 1));
		}

		MazeComponent mazeToSolve = new DepthFirstMaze(mazeGenerator.width / mazeGenerator.dilation, mazeGenerator.height / mazeGenerator.dilation, random);
		mazeToSolve.generateMaze();
		DepthFirstMazeSolver solvedMaze = new DepthFirstMazeSolver(mazeToSolve, start, endings, random);
		solvedMaze.generateMaze();

		MazeComponent maze = new DilateMaze(solvedMaze, mazeGenerator.dilation);
		maze.generateMaze();

		return maze;
	}

	public void decorateCell(BlockPos pos, BlockPos mazePos, MazeComponent maze, CellState state, int thickness, ChunkRegion region) {
		RandomGenerator random = RandomGenerator.createLegacy(mazeGenerator.blockSeed(pos.getX(), mazeGenerator.blockSeed(mazePos.getZ(), region.getSeed(), mazePos.getX()), pos.getZ()));
		Pair<RectangularMazePiece, BlockRotation> mazeSegment = RectangularMazePiece.getFromCell(state, random);
		if (mazeSegment.getFirst() != RectangularMazePiece.BLANK) {
			placeNbt(getPiece(mazeSegment.getFirst(), random), getPieceAsBottom(mazeSegment.getFirst(), random), this, region, pos, mazeSegment.getSecond());
		} else if (random.nextInt(67) == 0) {
			BlockPos offset = pos.add(random.nextInt(7), 0, random.nextInt(7));
			this.generateNbt(region, offset.add(0, 264, 0), "hoary_crossroads_obelisk_" + random.nextInt(5), BlockRotation.random(random));
			for (int i = 0; i < 264; i++) {
				region.setBlockState(offset.add(0, i, 0), Blocks.POLISHED_DEEPSLATE.getDefaultState(), Block.FORCE_STATE);
				region.setBlockState(offset.add(1, i, 0), Blocks.POLISHED_DEEPSLATE.getDefaultState(), Block.FORCE_STATE);
				region.setBlockState(offset.add(1, i, 1), Blocks.POLISHED_DEEPSLATE.getDefaultState(), Block.FORCE_STATE);
				region.setBlockState(offset.add(0, i, 1), Blocks.POLISHED_DEEPSLATE.getDefaultState(), Block.FORCE_STATE);
			}
		}
	}

	private void placeNbt(String nbt, String bottomNbt, AbstractNbtChunkGenerator chunkGenerator, ChunkRegion region, BlockPos basePos, BlockRotation rotation) {
		chunkGenerator.generateNbt(region, basePos.up(256), nbt, rotation);
		for (int i = 0; i < 256; i++) {
			chunkGenerator.generateNbt(region, basePos.up(i), bottomNbt, rotation);
		}
	}

	public String getPiece(RectangularMazePiece piece, RandomGenerator random) {
		switch (piece) {
		case F_PIECE:
			return "hoary_crossroads_f_" + ((random.nextInt(10) == 0) ? ((random.nextInt(8) == 0) ? "rare_0" : random.nextInt(8)) : "0");
		case I_PIECE:
			return "hoary_crossroads_i_" + ((random.nextInt(10) == 0) ? ((random.nextInt(8) == 0) ? "rare_0" : random.nextInt(10)) : "0");
		case L_PIECE:
			return "hoary_crossroads_l_" + ((random.nextInt(10) == 0) ? ((random.nextInt(8) == 0) ? "rare_0" : random.nextInt(10)) : "0");
		case NUB:
			return "hoary_crossroads_nub_" + ((random.nextInt(10) == 0) ? random.nextInt(8) : "0");
		case T_PIECE:
			return "hoary_crossroads_t_" + ((random.nextInt(10) == 0) ? ((random.nextInt(8) == 0) ? ("rare_" + random.nextInt(1)) : random.nextInt(8)) : "0");
		default:
			return "hoary_crossroads_nub_0";
		}
	}

	public String getPieceAsBottom(RectangularMazePiece piece, RandomGenerator random) {
		switch (piece) {
		case F_PIECE:
			return "hoary_crossroads_f_bottom";
		case I_PIECE:
			return "hoary_crossroads_i_bottom";
		case L_PIECE:
			return "hoary_crossroads_l_bottom";
		case NUB:
			return "hoary_crossroads_nub_bottom";
		case T_PIECE:
			return "hoary_crossroads_t_bottom";
		default:
			return "hoary_crossroads_nub_bottom";
		}
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
	public int getChunkDistance() {
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
