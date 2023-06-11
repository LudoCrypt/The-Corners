package net.ludocrypt.corners.world.chunk;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.init.CornerWorlds;
import net.ludocrypt.corners.world.maze.AndOrMaze;
import net.ludocrypt.corners.world.maze.GrandMazeGenerator;
import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.DepthFirstMaze;
import net.ludocrypt.limlib.api.world.maze.DepthFirstMazeSolver;
import net.ludocrypt.limlib.api.world.maze.DilateMaze;
import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.ludocrypt.limlib.api.world.maze.RectangularMazePiece;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.ChunkGenerator;

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

	private GrandMazeGenerator mazeGenerator;
	private int mazeWidth;
	private int mazeHeight;
	private int mazeDilation;
	private long mazeSeedModifier;

	public HoaryCrossroadsChunkGenerator(BiomeSource biomeSource, int mazeWidth, int mazeHeight, int mazeDilation, long mazeSeedModifier) {
		super(biomeSource, TheCorners.id(CornerWorlds.HOARY_CROSSROADS));
		this.mazeWidth = mazeWidth;
		this.mazeHeight = mazeHeight;
		this.mazeDilation = mazeDilation;
		this.mazeSeedModifier = mazeSeedModifier;
		this.mazeGenerator = new GrandMazeGenerator(this.mazeWidth, this.mazeHeight, this.mazeDilation, this.mazeSeedModifier);
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator,
			StructureTemplateManager structureTemplateManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> fullChunkConverter,
			List<Chunk> chunks, Chunk chunk) {
		BlockPos startPos = chunk.getPos().getStartPos();
		this.mazeGenerator.generateMaze(startPos, region.getSeed(), this::newMaze, (pos, mazePos, maze, cellState, thickness) -> decorateCell(pos, mazePos, maze, cellState, thickness, region));

		return CompletableFuture.completedFuture(chunk);
	}

	/**
	 * Create a new solved maze, with the starting and ending points based on a
	 * bigger maze called grandMaze.
	 * 
	 * @param mazePos the position of the maze
	 * @param width   width of the maze
	 * @param height  height of the maze
	 * @param random  generator
	 * @return MazeComponent
	 */
	public MazeComponent newMaze(BlockPos mazePos, int width, int height, RandomGenerator random) {
		// Find the position of the grandMaze that contains the current maze
		BlockPos grandMazePos = new BlockPos(mazePos.getX() - mazeGenerator.mod(mazePos.getX(), (mazeGenerator.width * mazeGenerator.width * mazeGenerator.thickness)), 0,
				mazePos.getZ() - mazeGenerator.mod(mazePos.getZ(), (mazeGenerator.height * mazeGenerator.height * mazeGenerator.thickness)));

		// Check if the grandMaze was already generated, if not generate it
		MazeComponent grandMaze;
		if (mazeGenerator.grandMazeMap.containsKey(grandMazePos)) {
			grandMaze = mazeGenerator.grandMazeMap.get(grandMazePos);
		} else {
			grandMaze = new DepthFirstMaze(mazeGenerator.width / mazeGenerator.dilation, mazeGenerator.height / mazeGenerator.dilation,
					RandomGenerator.createLegacy(mazeGenerator.blockSeed(grandMazePos.getX(), mazeGenerator.seedModifier, grandMazePos.getZ())));
			grandMaze.generateMaze();
			mazeGenerator.grandMazeMap.put(grandMazePos, grandMaze);
		}

		// Get the cell of the grandMaze that corresponds to the current maze
		CellState originCell = grandMaze.cellState((((mazePos.getX() - grandMazePos.getX()) / mazeGenerator.thickness) / mazeGenerator.width) / mazeGenerator.dilation,
				(((mazePos.getZ() - grandMazePos.getZ()) / mazeGenerator.thickness) / height) / mazeGenerator.dilation);

		Vec2i start = null;
		List<Vec2i> endings = Lists.newArrayList();

		// Check if the origin cell has an opening in the south or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set.
		if (originCell.isSouth() || originCell.getPosition().getX() == 0) {
			if (start == null) {
				start = new Vec2i(0, (mazeGenerator.height / mazeGenerator.dilation) / 2);
			}
		}

		// Check if the origin cell has an opening in the west or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set.
		if (originCell.isWest() || originCell.getPosition().getY() == 0) {
			if (start == null) {
				start = new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2, 0);
			} else {
				endings.add(new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2, 0));
			}
		}

		// Check if the origin cell has an opening in the north or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set. Else add an ending point to the middle of that side.
		if (originCell.isNorth() || originCell.getPosition().getX() == (mazeGenerator.width / mazeGenerator.dilation) - 1) {
			if (start == null) {
				start = new Vec2i((mazeGenerator.width / mazeGenerator.dilation) - 1, (mazeGenerator.height / mazeGenerator.dilation) / 2);
			} else {
				endings.add(new Vec2i((mazeGenerator.width / mazeGenerator.dilation) - 1, (mazeGenerator.height / mazeGenerator.dilation) / 2));
			}
		}

		// Check if the origin cell has an opening in the east or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set. Else add an ending point to the middle of that side.
		if (originCell.isEast() || originCell.getPosition().getY() == (mazeGenerator.height / mazeGenerator.dilation) - 1) {
			if (start == null) {
				start = new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2, (mazeGenerator.height / mazeGenerator.dilation) - 1);
			} else {
				endings.add(new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2, (mazeGenerator.height / mazeGenerator.dilation) - 1));
			}
		}

		// If the origin cell is a dead end, add a random ending point in the middle of
		// the maze. This ensures there is always somewhere to go in a dead end.
		if (endings.isEmpty()) {
			endings.add(new Vec2i(random.nextInt((mazeGenerator.width / mazeGenerator.dilation) - 2) + 1, random.nextInt((mazeGenerator.height / mazeGenerator.dilation) - 2) + 1));
		}

		// Create a new maze.
		MazeComponent mazeToSolve = new DepthFirstMaze(mazeGenerator.width / mazeGenerator.dilation, mazeGenerator.height / mazeGenerator.dilation, random);
		mazeToSolve.generateMaze();

		// Create a maze solver and solve the maze using the starting point and ending
		// points.
		MazeComponent solvedMaze = new DepthFirstMazeSolver(mazeToSolve, start, endings, random);
		solvedMaze.generateMaze();

		// Create a scaled maze using the dilation.
		MazeComponent dilatedMaze = new DilateMaze(solvedMaze, mazeGenerator.dilation);
		dilatedMaze.generateMaze();

		Vec2i starting = new Vec2i(random.nextInt((dilatedMaze.width / 2) - 2) + 1, random.nextInt((dilatedMaze.height / 2) - 2) + 1);
		Vec2i ending = new Vec2i(random.nextInt((dilatedMaze.width / 2) - 2) + 1, random.nextInt((dilatedMaze.height / 2) - 2) + 1);

		// Make a new maze
		MazeComponent overlayMaze = new DepthFirstMaze(dilatedMaze.width / 2, dilatedMaze.height / 2, random);
		overlayMaze.generateMaze();

		// Find a path along two random points
		MazeComponent solvedOverlay = new DepthFirstMazeSolver(overlayMaze, starting, List.of(ending), random);
		solvedOverlay.generateMaze();

		// Make it bigger
		MazeComponent dilatedOverlay = new DilateMaze(solvedOverlay, 2);
		dilatedOverlay.generateMaze();

		// Combine the two
		AndOrMaze combinedMaze = new AndOrMaze(dilatedMaze, dilatedOverlay);
		combinedMaze.generateMaze();

		return combinedMaze;
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
			return "hoary_crossroads_f_" + ((random.nextInt(8) == 0) ? ((random.nextInt(13) == 0) ? "rare_0" : random.nextInt(8)) : "0");
		case I_PIECE:
			return "hoary_crossroads_i_" + ((random.nextInt(8) == 0) ? ((random.nextInt(13) == 0) ? "rare_0" : random.nextInt(10)) : "0");
		case L_PIECE:
			return "hoary_crossroads_l_" + ((random.nextInt(8) == 0) ? ((random.nextInt(13) == 0) ? "rare_0" : random.nextInt(10)) : "0");
		case NUB:
			return "hoary_crossroads_nub_" + ((random.nextInt(8) == 0) ? random.nextInt(13) : "0");
		case T_PIECE:
			return "hoary_crossroads_t_" + ((random.nextInt(8) == 0) ? ((random.nextInt(13) == 0) ? ("rare_" + random.nextInt(1)) : random.nextInt(8)) : "0");
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
			if (region.getBlockEntity(pos) instanceof LootableContainerBlockEntity lootTable) {
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

	@Override
	public void method_40450(List<String> list, RandomState randomState, BlockPos pos) {

	}

}
