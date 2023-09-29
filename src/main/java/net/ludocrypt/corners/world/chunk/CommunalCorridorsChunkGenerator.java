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
import net.ludocrypt.limlib.api.world.maze.RectangularMazeGenerator;
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

	private GrandMazeGenerator grandMazeGenerator;
	private RectangularMazeGenerator<MazeComponent> level2mazeGenerator;
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
		this.grandMazeGenerator = new GrandMazeGenerator(this.mazeWidth, this.mazeHeight, this.mazeDilation, this.mazeSeedModifier);
		this.level2mazeGenerator = new RectangularMazeGenerator<MazeComponent>(this.mazeWidth * this.mazeDilation, this.mazeHeight * this.mazeDilation, this.mazeDilation, true,
				this.mazeSeedModifier) {
		};
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
	public MazeComponent newGrandMaze(BlockPos mazePos, int width, int height, RandomGenerator random) {
		// Find the position of the grandMaze that contains the current maze
		BlockPos grandMazePos = new BlockPos(mazePos.getX() - Math.floorMod(mazePos.getX(), (grandMazeGenerator.width * grandMazeGenerator.width * grandMazeGenerator.thickness)), 0,
				mazePos.getZ() - Math.floorMod(mazePos.getZ(), (grandMazeGenerator.height * grandMazeGenerator.height * grandMazeGenerator.thickness)));

		// Check if the grandMaze was already generated, if not generate it
		MazeComponent grandMaze;
		if (grandMazeGenerator.grandMazeMap.containsKey(grandMazePos)) {
			grandMaze = grandMazeGenerator.grandMazeMap.get(grandMazePos);
		} else {
			grandMaze = new DepthFirstMaze(grandMazeGenerator.width / grandMazeGenerator.dilation, grandMazeGenerator.height / grandMazeGenerator.dilation,
					RandomGenerator.createLegacy(grandMazeGenerator.blockSeed(grandMazePos.getX(), grandMazeGenerator.seedModifier, grandMazePos.getZ())));
			grandMaze.generateMaze();
			grandMazeGenerator.grandMazeMap.put(grandMazePos, grandMaze);
		}

		// Get the cell of the grandMaze that corresponds to the current maze
		CellState originCell = grandMaze.cellState((((mazePos.getX() - grandMazePos.getX()) / grandMazeGenerator.thickness) / grandMazeGenerator.width) / grandMazeGenerator.dilation,
				(((mazePos.getZ() - grandMazePos.getZ()) / grandMazeGenerator.thickness) / height) / grandMazeGenerator.dilation);

		Vec2i start = null;
		List<Vec2i> endings = Lists.newArrayList();

		// Check if the origin cell has an opening in the south or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set.
		if (originCell.isSouth() || originCell.getPosition().getX() == 0) {
			if (start == null) {
				start = new Vec2i(0, (grandMazeGenerator.height / grandMazeGenerator.dilation) / 2);
			}
		}

		// Check if the origin cell has an opening in the west or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set.
		if (originCell.isWest() || originCell.getPosition().getY() == 0) {
			if (start == null) {
				start = new Vec2i((grandMazeGenerator.width / grandMazeGenerator.dilation) / 2, 0);
			} else {
				endings.add(new Vec2i((grandMazeGenerator.width / grandMazeGenerator.dilation) / 2, 0));
			}
		}

		// Check if the origin cell has an opening in the north or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set. Else add an ending point to the middle of that side.
		if (originCell.isNorth() || originCell.getPosition().getX() == (grandMazeGenerator.width / grandMazeGenerator.dilation) - 1) {
			if (start == null) {
				start = new Vec2i((grandMazeGenerator.width / grandMazeGenerator.dilation) - 1, (grandMazeGenerator.height / grandMazeGenerator.dilation) / 2);
			} else {
				endings.add(new Vec2i((grandMazeGenerator.width / grandMazeGenerator.dilation) - 1, (grandMazeGenerator.height / grandMazeGenerator.dilation) / 2));
			}
		}

		// Check if the origin cell has an opening in the east or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set. Else add an ending point to the middle of that side.
		if (originCell.isEast() || originCell.getPosition().getY() == (grandMazeGenerator.height / grandMazeGenerator.dilation) - 1) {
			if (start == null) {
				start = new Vec2i((grandMazeGenerator.width / grandMazeGenerator.dilation) / 2, (grandMazeGenerator.height / grandMazeGenerator.dilation) - 1);
			} else {
				endings.add(new Vec2i((grandMazeGenerator.width / grandMazeGenerator.dilation) / 2, (grandMazeGenerator.height / grandMazeGenerator.dilation) - 1));
			}
		}

		// If the origin cell is a dead end, add a random ending point in the middle of
		// the maze. This ensures there is always somewhere to go in a dead end.
		if (endings.isEmpty()) {
			endings.add(new Vec2i(random.nextInt((grandMazeGenerator.width / grandMazeGenerator.dilation) - 2) + 1, random.nextInt((grandMazeGenerator.height / grandMazeGenerator.dilation) - 2) + 1));
		}

		// Create a new maze.
		MazeComponent mazeToSolve = new StraightDepthFirstMaze(grandMazeGenerator.width / grandMazeGenerator.dilation, grandMazeGenerator.height / grandMazeGenerator.dilation, random, 0.45D);
		mazeToSolve.generateMaze();

		// Create a maze solver and solve the maze using the starting point and ending
		// points.
		MazeComponent solvedMaze = new DepthFirstMazeSolver(mazeToSolve, start, endings, random);
		solvedMaze.generateMaze();

		// Create a scaled maze using the dilation.
		MazeComponent dilatedMaze = new DilateMaze(solvedMaze, grandMazeGenerator.dilation);
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

	public MazeComponent newMaze(BlockPos mazePos, int width, int height, RandomGenerator random) {
		MazeComponent maze = new DepthFirstMaze(width, height, random);
		maze.generateMaze();
		return maze;
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator,
			StructureTemplateManager structureTemplateManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> fullChunkConverter,
			List<Chunk> chunks, Chunk chunk) {
		BlockPos startPos = chunk.getPos().getStartPos();
		this.grandMazeGenerator.generateMaze(startPos, region.getSeed(), this::newGrandMaze,
				(pos, mazePos, maze, cellState, thickness) -> decorateGrandCell(pos, mazePos, maze, cellState, thickness, region));
		this.level2mazeGenerator.generateMaze(startPos, region.getSeed(), this::newMaze, (pos, mazePos, maze, cellState, thickness) -> decorateCell(pos, mazePos, maze, cellState, thickness, region));
		return CompletableFuture.completedFuture(chunk);
	}

	public void decorateCell(BlockPos pos, BlockPos mazePos, MazeComponent maze, CellState state, int thickness, ChunkRegion region) {
		RandomGenerator random = RandomGenerator.createLegacy(grandMazeGenerator.blockSeed(pos.getX(), grandMazeGenerator.blockSeed(mazePos.getZ(), region.getSeed(), mazePos.getX()), pos.getZ()));

		RandomGenerator chunkRandom = RandomGenerator
				.createLegacy(region.getSeed() + MathHelper.hashCode(pos.getX() - Math.floorMod(pos.getX(), 16), pos.getZ() - Math.floorMod(pos.getZ(), 16), -1337));
		RandomGenerator bigRandom = RandomGenerator.createLegacy(region.getSeed() + MathHelper.hashCode(pos.getX() - Math.floorMod(pos.getX(), 32), pos.getZ() - Math.floorMod(pos.getZ(), 32), 69420));
		RandomGenerator hugeRandom = RandomGenerator.createLegacy(region.getSeed() + MathHelper.hashCode(pos.getX() - Math.floorMod(pos.getX(), 64), pos.getZ() - Math.floorMod(pos.getZ(), 64), 1337));

		if (hugeRandom.nextDouble() < 0.04522689D) {
			boolean flip = hugeRandom.nextBoolean();

			int xOffset = MathHelper.floor(((double) Math.floorMod(pos.getX(), 64)) / 16.0D) * 16;
			int zOffset = MathHelper.floor(((double) Math.floorMod(pos.getZ(), 64)) / 16.0D) * 16;

			if (Math.floorMod(pos.getX(), 16) == 0 && Math.floorMod(pos.getZ(), 16) == 0) {
				generateNbt(region, new BlockPos(xOffset, 0, zOffset), pos.up(17), pos.up(17).add(16, 64, 16), "level2/communal_corridors_level2_decorated_huge_" + (hugeRandom.nextInt(2)),
						flip ? BlockRotation.CLOCKWISE_90 : BlockRotation.NONE, flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
			}
			return;
		} else if (bigRandom.nextDouble() < 0.09888567D) {
			boolean flip = bigRandom.nextBoolean();

			int xOffset = MathHelper.floor(((double) Math.floorMod(pos.getX(), 32)) / 16.0D) * 16;
			int zOffset = MathHelper.floor(((double) Math.floorMod(pos.getZ(), 32)) / 16.0D) * 16;

			if (Math.floorMod(pos.getX(), 16) == 0 && Math.floorMod(pos.getZ(), 16) == 0) {
				int i = bigRandom.nextInt(5);
				if (i == 4) {
					generateNbt(region, new BlockPos(xOffset, 0, zOffset), pos.up(15), pos.up(15).add(16, 64, 16), "level2/communal_corridors_level2_decorated_big_dip_0",
							flip ? BlockRotation.CLOCKWISE_90 : BlockRotation.NONE, flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
				} else {
					generateNbt(region, new BlockPos(xOffset, 0, zOffset), pos.up(17), pos.up(17).add(16, 64, 16), "level2/communal_corridors_level2_decorated_big_" + i,
							flip ? BlockRotation.CLOCKWISE_90 : BlockRotation.NONE, flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
				}
			}
			return;
		} else {
			if (chunkRandom.nextDouble() < 0.07624375D) {
				boolean flip = chunkRandom.nextBoolean();
				if (Math.floorMod(pos.getX(), 16) == 0 && Math.floorMod(pos.getZ(), 16) == 0) {
					if (chunkRandom.nextDouble() < 0.7624375D) {
						generateNbt(region, pos.up(17), "level2/communal_corridors_level2_decorated_" + (chunkRandom.nextInt(12)), flip ? BlockRotation.CLOCKWISE_90 : BlockRotation.NONE,
								flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
					} else {
						if ((chunkRandom.nextDouble() < 0.31275D && chunkRandom.nextInt(8) == 0)) {
							generateNbt(region, pos.up(17), "level2/communal_corridors_level2_tall_" + (chunkRandom.nextInt(12) + 1), flip ? BlockRotation.CLOCKWISE_90 : BlockRotation.NONE,
									flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
						} else {
							generateNbt(region, pos.up(17), "level2/communal_corridors_level2_" + (chunkRandom.nextInt(12) + 1), flip ? BlockRotation.CLOCKWISE_90 : BlockRotation.NONE,
									flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
						}
					}
				}
				return;
			}
		}

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

			if (random.nextDouble() > 0.67289445D) {
				this.generateNbt(region, pos.up(17), "maze/communal_corridors_" + dir + "_" + (random.nextInt(10)));
			} else {
				this.generateNbt(region, pos.up(17), "maze/communal_corridors_" + dir + "_decorated_" + (random.nextInt(10)));
			}

		}
	}

	public void decorateGrandCell(BlockPos pos, BlockPos mazePos, MazeComponent maze, CellState state, int thickness, ChunkRegion region) {

		for (int x = 0; x < thickness; x++) {
			for (int z = 0; z < thickness; z++) {
				region.setBlockState(pos.add(x, 0, z), Blocks.OAK_PLANKS.getDefaultState(), Block.FORCE_STATE, 0);
				region.setBlockState(pos.add(x, 16, z), Blocks.OAK_PLANKS.getDefaultState(), Block.FORCE_STATE, 0);
			}
		}

		RandomGenerator random = RandomGenerator.createLegacy(grandMazeGenerator.blockSeed(pos.getX(), grandMazeGenerator.blockSeed(mazePos.getZ(), region.getSeed(), mazePos.getX()), pos.getZ()));

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

			if (random.nextDouble() > 0.67289445D) {
				this.generateNbt(region, pos.up(1), "maze/communal_corridors_" + dir + "_" + (random.nextInt(10)));
			} else {
				this.generateNbt(region, pos.up(1), "maze/communal_corridors_" + dir + "_decorated_" + (random.nextInt(10)));
			}

		} else {

			for (int x = 0; x < thickness; x++) {
				for (int z = 0; z < thickness; z++) {
					for (int y = 0; y < 8; y++) {
						region.setBlockState(pos.add(x, 6 + y, z), CornerBlocks.DRYWALL.getDefaultState(), Block.FORCE_STATE, 0);
					}
				}
			}

			RandomGenerator fullChunkRandom = RandomGenerator
					.createLegacy(region.getSeed() + MathHelper.hashCode(pos.getX() - Math.floorMod(pos.getX(), 16), pos.getZ() - Math.floorMod(pos.getZ(), 16), -69420));

			boolean flip = random.nextBoolean();
			boolean shouldGenerateSmall = true;

			Vec2i fullChunkPos = new Vec2i(state.getPosition().getX() - Math.floorMod(state.getPosition().getX(), 2), state.getPosition().getY() - Math.floorMod(state.getPosition().getY(), 2));

			if ((fullChunkRandom.nextDouble() < 0.31275D && fullChunkRandom.nextInt(8) == 0)) {
				if (!(maze.cellState(fullChunkPos).isWest() || maze.cellState(fullChunkPos).isNorth() || maze.cellState(fullChunkPos).isEast() || maze.cellState(fullChunkPos).isSouth())) {
					if (Math.floorMod(state.getPosition().getX(), 2) == 0 && Math.floorMod(state.getPosition().getY(), 2) == 0) {
						generateNbt(region, pos.up(), "communal_corridors_decorated_big_" + (fullChunkRandom.nextInt(3) + 1), flip ? BlockRotation.COUNTERCLOCKWISE_90 : BlockRotation.NONE,
								flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
					}

					shouldGenerateSmall = false;
				}
			}

			if (shouldGenerateSmall) {
				if (random.nextDouble() < 0.2375625D) {
					generateNbt(region, pos.up(), "communal_corridors_" + (random.nextInt(14) + 1), flip ? BlockRotation.COUNTERCLOCKWISE_90 : BlockRotation.NONE,
							flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
				} else {
					generateNbt(region, pos.up(), "communal_corridors_decorated_" + (random.nextInt(26) + 1), flip ? BlockRotation.COUNTERCLOCKWISE_90 : BlockRotation.NONE,
							flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
				}
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

			store("maze/communal_corridors_" + dir, world, 0, 9);
			store("maze/communal_corridors_" + dir + "_decorated", world, 0, 9);
		}

		store("communal_corridors", world, 1, 14);
		store("communal_corridors_decorated", world, 1, 26);
		store("communal_corridors_decorated_big", world, 1, 3);

		store("level2/communal_corridors_level2", world, 1, 12);
		store("level2/communal_corridors_level2_tall", world, 1, 12);

		store("level2/communal_corridors_level2_decorated", world, 0, 11);
		store("level2/communal_corridors_level2_decorated_big", world, 0, 3);
		store("level2/communal_corridors_level2_decorated_big_dip_0", world);
		store("level2/communal_corridors_level2_decorated_huge", world, 0, 1);
	}

	@Override
	public int getChunkDistance() {
		return 2;
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
		} else if (state.isOf(Blocks.RED_STAINED_GLASS)) {
			RandomGenerator random = RandomGenerator.createLegacy(region.getSeed() + MathHelper.hashCode(pos));
			if (random.nextDouble() < 0.3765568D) {
				region.setBlockState(pos, Blocks.COBWEB.getDefaultState(), Block.NOTIFY_ALL, 1);
			} else {
				region.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL, 1);
			}
		} else if (state.isOf(Blocks.CHEST)) {
			region.setBlockState(pos, state, Block.NOTIFY_ALL, 1);
			if (region.getBlockEntity(pos) instanceof LootableContainerBlockEntity lootTable) {
				lootTable.setLootTable(LootTables.WOODLAND_MANSION_CHEST, region.getSeed() + MathHelper.hashCode(pos));
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
