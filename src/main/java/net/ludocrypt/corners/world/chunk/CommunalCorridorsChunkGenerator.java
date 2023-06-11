package net.ludocrypt.corners.world.chunk;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.block.RadioBlock;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerWorlds;
import net.ludocrypt.corners.world.maze.AndOrMaze;
import net.ludocrypt.corners.world.maze.GrandMazeGenerator;
import net.ludocrypt.corners.world.maze.StraightDepthFirstMaze;
import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.DepthFirstMaze;
import net.ludocrypt.limlib.api.world.maze.DepthFirstMazeSolver;
import net.ludocrypt.limlib.api.world.maze.DilateMaze;
import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
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

public class CommunalCorridorsChunkGenerator extends AbstractNbtChunkGenerator {

	public static final Codec<CommunalCorridorsChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
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
		})).apply(instance, instance.stable(CommunalCorridorsChunkGenerator::new));
	});

	private GrandMazeGenerator mazeGenerator;
	private int mazeWidth;
	private int mazeHeight;
	private int mazeDilation;
	private long mazeSeedModifier;

	public CommunalCorridorsChunkGenerator(BiomeSource biomeSource, int mazeWidth, int mazeHeight, int mazeDilation, long mazeSeedModifier) {
		super(biomeSource, TheCorners.id(CornerWorlds.COMMUNAL_CORRIDORS));
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
		MazeComponent mazeToSolve = new StraightDepthFirstMaze(mazeGenerator.width / mazeGenerator.dilation, mazeGenerator.height / mazeGenerator.dilation, random, 0.45D);
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
		MazeComponent overlayMaze = new StraightDepthFirstMaze(dilatedMaze.width / 2, dilatedMaze.height / 2, random, 0.7D);
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

	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator,
			StructureTemplateManager structureTemplateManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> fullChunkConverter,
			List<Chunk> chunks, Chunk chunk) {
		BlockPos startPos = chunk.getPos().getStartPos();
		this.mazeGenerator.generateMaze(startPos, region.getSeed(), this::newMaze, (pos, mazePos, maze, cellState, thickness) -> decorateCell(pos, mazePos, maze, cellState, thickness, region));

		return CompletableFuture.completedFuture(chunk);
	}

	public void decorateCell(BlockPos pos, BlockPos mazePos, MazeComponent maze, CellState state, int thickness, ChunkRegion region) {

		for (int x = 0; x < thickness; x++) {
			for (int z = 0; z < thickness; z++) {
				region.setBlockState(pos.add(x, 0, z), Blocks.OAK_PLANKS.getDefaultState(), Block.FORCE_STATE, 0);
			}
		}

		RandomGenerator random = RandomGenerator.createLegacy(mazeGenerator.blockSeed(pos.getX(), mazeGenerator.blockSeed(mazePos.getZ(), region.getSeed(), mazePos.getX()), pos.getZ()));

		BlockRotation rotation = BlockRotation.NONE;
		BlockMirror mirror = BlockMirror.NONE;

		String dir = "nesw";

		if (!state.isWest()) {
			dir = dir.replace("n", "");
		}
		if (!state.isNorth()) {
			dir = dir.replace("e", "");
		}
		if (!state.isEast()) {
			dir = dir.replace("s", "");
		}
		if (!state.isSouth()) {
			dir = dir.replace("w", "");
		}
		if (dir != "") {

			if (random.nextDouble() < 0.35) {
				dir = dir + "_" + (random.nextInt(4) + 1);
			}

			this.generateNbt(region, pos.up(1), "maze/communal_corridors_" + dir, rotation, mirror);
		} else {

			RandomGenerator fullChunkRandom = RandomGenerator.createLegacy(region.getSeed() + MathHelper.hashCode(pos.getX() - (pos.getX() % 16), pos.getZ() - (pos.getZ() % 16), -69420));

			if ((fullChunkRandom.nextDouble() < 0.31275D && fullChunkRandom.nextInt(8) == 0)) {
				if (maze.fits(new Vec2i(state.getPosition().getX() + 1, state.getPosition().getY())) && maze.fits(new Vec2i(state.getPosition().getX(), state.getPosition().getY() + 1))
						&& maze.fits(new Vec2i(state.getPosition().getX() + 1, state.getPosition().getY() + 1))) {
					if (!(maze.hasNeighbors(new Vec2i(state.getPosition().getX() + 1, state.getPosition().getY()))
							|| maze.hasNeighbors(new Vec2i(state.getPosition().getX(), state.getPosition().getY() + 1))
							|| maze.hasNeighbors(new Vec2i(state.getPosition().getX() + 1, state.getPosition().getY() + 1)))) {
						generateNbt(region, pos.up(), "communal_corridors_decorated_big_" + (fullChunkRandom.nextInt(3) + 1));
						return;
					}
				}
			}

			if (random.nextDouble() < 0.2375625D) {
				generateNbt(region, pos.up(), "communal_corridors_" + (random.nextInt(5) + 1));
			} else {
				generateNbt(region, pos.up(), "communal_corridors_decorated_" + (random.nextInt(22) + 1));
			}
		}

	}

	@Override
	public void storeStructures(ServerWorld world) {
		for (int i = 0; i < 15; i++) {
			String dir = "nesw";
			boolean north = ((i & 8) != 0);
			boolean east = ((i & 4) != 0);
			boolean south = ((i & 2) != 0);
			boolean west = ((i & 1) != 0);

			if (north) {
				dir = dir.replace("n", "");
			}
			if (east) {
				dir = dir.replace("e", "");
			}
			if (south) {
				dir = dir.replace("s", "");
			}
			if (west) {
				dir = dir.replace("w", "");
			}

			store("maze/communal_corridors_" + dir, world);
			store("maze/communal_corridors_" + dir, world, 1, 4);
		}

		store("communal_corridors", world, 1, 5);
		store("communal_corridors_decorated", world, 1, 22);
		store("communal_corridors_decorated_big", world, 1, 3);
	}

	@Override
	public int getChunkDistance() {
		return 1;
	}

	@Override
	protected Identifier getBarrelLootTable() {
		return LootTables.SPAWN_BONUS_CHEST;
	}

	@Override
	protected void modifyStructure(ChunkRegion region, BlockPos pos, BlockState state, NbtCompound nbt) {
		super.modifyStructure(region, pos, state, nbt);
		if (state.isOf(CornerBlocks.WOODEN_RADIO)) {
			int i = RandomGenerator.createLegacy(region.getSeed() + MathHelper.hashCode(pos)).nextInt(3);
			switch (i) {
			case 1:
				region.setBlockState(pos, CornerBlocks.TUNED_RADIO.getDefaultState().with(RadioBlock.FACING, state.get(RadioBlock.FACING)), Block.NOTIFY_ALL, 1);
				break;
			case 2:
				region.setBlockState(pos, CornerBlocks.BROKEN_RADIO.getDefaultState().with(RadioBlock.FACING, state.get(RadioBlock.FACING)), Block.NOTIFY_ALL, 1);
				break;
			case 0:
			default:
				break;
			}
		}
	}

	@Override
	public int getWorldHeight() {
		return 128;
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
