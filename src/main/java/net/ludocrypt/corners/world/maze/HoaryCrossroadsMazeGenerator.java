package net.ludocrypt.corners.world.maze;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.limlib.api.world.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.DepthFirstMaze;
import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.ludocrypt.limlib.api.world.maze.MazeGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class HoaryCrossroadsMazeGenerator extends MazeGenerator<AbstractNbtChunkGenerator, DepthFirstMazeSolver> {

	public static final Codec<HoaryCrossroadsMazeGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.INT.fieldOf("width").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.width;
		}), Codec.INT.fieldOf("height").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.height;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.seedModifier;
		})).apply(instance, instance.stable(HoaryCrossroadsMazeGenerator::new));
	});

	private final HashMap<BlockPos, MazeComponent> grandMazeMap = new HashMap<BlockPos, MazeComponent>(30);

	public HoaryCrossroadsMazeGenerator(int width, int height, long seedModifier) {
		super(width, height, 16, false, seedModifier);
	}

	@Override
	public DepthFirstMazeSolver newMaze(BlockPos mazePos, ChunkRegion region, Chunk chunk, AbstractNbtChunkGenerator chunkGenerator, int width, int height, Random random) {
		BlockPos grandMazePos = new BlockPos(mazePos.getX() - mod(mazePos.getX(), (width * width * thickness)), 0, mazePos.getZ() - mod(mazePos.getZ(), (height * height * thickness)));

		MazeComponent grandMaze;
		if (this.grandMazeMap.containsKey(grandMazePos)) {
			grandMaze = this.grandMazeMap.get(grandMazePos);
		} else {
			grandMaze = new DepthFirstMaze(width, height, random);
			grandMaze.generateMaze();
			this.grandMazeMap.put(grandMazePos, grandMaze);
		}

		CellState originCell = grandMaze.cellState((mazePos.getX() - grandMazePos.getX()) / thickness, (mazePos.getZ() - grandMazePos.getZ()) / thickness);

		Random grandRand = new Random(blockSeed(grandMazePos.getX(), blockSeed(mazePos.getX(), this.seedModifier, mazePos.getZ()), grandMazePos.getZ()));
		Random grandRandNorth = new Random(blockSeed(grandMazePos.getX(), blockSeed(mazePos.getX() + (width * thickness), this.seedModifier, mazePos.getZ()), grandMazePos.getZ()));
		Random grandRandEast = new Random(blockSeed(grandMazePos.getX(), blockSeed(mazePos.getX(), this.seedModifier, mazePos.getZ() + (width * thickness)), grandMazePos.getZ()));
		Random grandRandSouth = new Random(blockSeed(grandMazePos.getX(), blockSeed(mazePos.getX() - (width * thickness), this.seedModifier, mazePos.getZ()), grandMazePos.getZ()));
		Random grandRandWest = new Random(blockSeed(grandMazePos.getX(), blockSeed(mazePos.getX(), this.seedModifier, mazePos.getZ() + (width * thickness)), grandMazePos.getZ()));

		Vec2i start = null;
		List<Vec2i> endings = Lists.newArrayList();

		if (originCell.getPosition().getX() == 0) {
			if (start == null) {
				start = new Vec2i(0, height / 2);
			}
		}

		if (originCell.getPosition().getY() == 0) {
			if (start == null) {
				start = new Vec2i(width / 2, 0);
			} else {
				endings.add(new Vec2i(width / 2, 0));
			}
		}

		if (originCell.getPosition().getX() == width - 1) {
			if (start == null) {
				start = new Vec2i(width - 1, height / 2);
			} else {
				endings.add(new Vec2i(width - 1, height / 2));
			}
		}

		if (originCell.getPosition().getY() == height - 1) {
			if (start == null) {
				start = new Vec2i(width / 2, 0);
			} else {
				endings.add(new Vec2i(width / 2, height - 1));
			}
		}

		if (originCell.isSouth()) {
			if (start == null) {
				start = new Vec2i(0, grandRand.nextInt(height));
			} else {
				endings.add(new Vec2i(0, grandRandSouth.nextInt(height)));
			}
		}

		if (originCell.isWest()) {
			if (start == null) {
				start = new Vec2i(grandRand.nextInt(width), 0);
			} else {
				endings.add(new Vec2i(grandRandWest.nextInt(width), 0));
			}
		}

		if (originCell.isNorth()) {
			if (start == null) {
				start = new Vec2i(width - 1, grandRand.nextInt(height));
			} else {
				endings.add(new Vec2i(width - 1, grandRandNorth.nextInt(height)));
			}
		}

		if (originCell.isEast()) {
			if (start == null) {
				start = new Vec2i(grandRand.nextInt(width), height - 1);
			} else {
				endings.add(new Vec2i(grandRandEast.nextInt(width), height - 1));
			}
		}

		MazeComponent mazeToSolve = new DepthFirstMaze(width, height, grandRand);
		mazeToSolve.generateMaze();
		DepthFirstMazeSolver maze = new DepthFirstMazeSolver(mazeToSolve, start, endings, grandRand);
		maze.generateMaze();
		return maze;
	}

	@Override
	public void decorateCell(BlockPos pos, BlockPos mazePos, Chunk chunk, ChunkRegion region, AbstractNbtChunkGenerator chunkGenerator, DepthFirstMazeSolver maze, CellState state, int thickness) {
		if (state.isNorth() && state.isEast() && state.isSouth() && state.isWest()) {
			placeNbt("hoary_crossroads_t", chunkGenerator, region, pos, BlockRotation.NONE, true);
		} else if (state.isNorth() && state.isEast() && state.isSouth() && !state.isWest()) {
			placeNbt("hoary_crossroads_f", chunkGenerator, region, pos, BlockRotation.CLOCKWISE_180, true);
		} else if (state.isNorth() && state.isEast() && !state.isSouth() && state.isWest()) {
			placeNbt("hoary_crossroads_f", chunkGenerator, region, pos, BlockRotation.CLOCKWISE_90, true);
		} else if (state.isNorth() && state.isEast() && !state.isSouth() && !state.isWest()) {
			placeNbt("hoary_crossroads_l", chunkGenerator, region, pos, BlockRotation.CLOCKWISE_90, true);
		} else if (state.isNorth() && !state.isEast() && state.isSouth() && state.isWest()) {
			placeNbt("hoary_crossroads_f", chunkGenerator, region, pos, BlockRotation.NONE, true);
		} else if (state.isNorth() && !state.isEast() && state.isSouth() && !state.isWest()) {
			placeNbt("hoary_crossroads_i", chunkGenerator, region, pos, BlockRotation.NONE, true);
		} else if (state.isNorth() && !state.isEast() && !state.isSouth() && state.isWest()) {
			placeNbt("hoary_crossroads_l", chunkGenerator, region, pos, BlockRotation.NONE, true);
		} else if (state.isNorth() && !state.isEast() && !state.isSouth() && !state.isWest()) {
			placeNbt("hoary_crossroads_nub", chunkGenerator, region, pos, BlockRotation.CLOCKWISE_90, true);
		} else if (!state.isNorth() && state.isEast() && state.isSouth() && state.isWest()) {
			placeNbt("hoary_crossroads_f", chunkGenerator, region, pos, BlockRotation.COUNTERCLOCKWISE_90, true);
		} else if (!state.isNorth() && state.isEast() && state.isSouth() && !state.isWest()) {
			placeNbt("hoary_crossroads_l", chunkGenerator, region, pos, BlockRotation.CLOCKWISE_180, true);
		} else if (!state.isNorth() && state.isEast() && !state.isSouth() && state.isWest()) {
			placeNbt("hoary_crossroads_i", chunkGenerator, region, pos, BlockRotation.CLOCKWISE_90, true);
		} else if (!state.isNorth() && state.isEast() && !state.isSouth() && !state.isWest()) {
			placeNbt("hoary_crossroads_nub", chunkGenerator, region, pos, BlockRotation.CLOCKWISE_180, true);
		} else if (!state.isNorth() && !state.isEast() && state.isSouth() && state.isWest()) {
			placeNbt("hoary_crossroads_l", chunkGenerator, region, pos, BlockRotation.COUNTERCLOCKWISE_90, true);
		} else if (!state.isNorth() && !state.isEast() && state.isSouth() && !state.isWest()) {
			placeNbt("hoary_crossroads_nub", chunkGenerator, region, pos, BlockRotation.COUNTERCLOCKWISE_90, true);
		} else if (!state.isNorth() && !state.isEast() && !state.isSouth() && state.isWest()) {
			placeNbt("hoary_crossroads_nub", chunkGenerator, region, pos, BlockRotation.NONE, true);
		}
	}

	private void placeNbt(String nbt, AbstractNbtChunkGenerator chunkGenerator, ChunkRegion region, BlockPos basePos, BlockRotation rotation, boolean generateFloor) {
		chunkGenerator.generateNbt(region, basePos.up(256), nbt, rotation);
		if (generateFloor) {
			for (int i = 0; i < 256; i++) {
				chunkGenerator.generateNbt(region, basePos.up(i), nbt + "_bottom", rotation);
			}
		}
	}

	@Override
	public Codec<? extends MazeGenerator<? extends ChunkGenerator, ? extends MazeComponent>> getCodec() {
		return CODEC;
	}

}
