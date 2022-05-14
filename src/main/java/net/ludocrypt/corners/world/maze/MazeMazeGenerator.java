package net.ludocrypt.corners.world.maze;

import java.util.Random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.limlib.api.world.maze.DepthFirstMaze;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.ludocrypt.limlib.api.world.maze.MazeGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class MazeMazeGenerator<C extends ChunkGenerator> extends MazeGenerator<C, DepthFirstMaze> {

	public static final Codec<MazeMazeGenerator<? extends ChunkGenerator>> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.INT.fieldOf("width").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.width;
		}), Codec.INT.fieldOf("height").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.height;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.seedModifier;
		})).apply(instance, instance.stable(MazeMazeGenerator::new));
	});

	public MazeMazeGenerator(int width, int height, long seedModifier) {
		super(width, height, 512, false, seedModifier);
	}

	@Override
	public DepthFirstMaze newMaze(BlockPos mazePos, ChunkRegion region, Chunk chunk, C chunkGenerator, int width, int height, Random random) {
		DepthFirstMaze mazeToSolve = new DepthFirstMaze(width, height, random);
		mazeToSolve.generateMaze();
		return mazeToSolve;
	}

	@Override
	public void decorateCell(BlockPos pos, BlockPos mazePos, Chunk chunk, ChunkRegion region, C chunkGenerator, DepthFirstMaze maze, CellState state, int thickness) {
	}

	@Override
	public Codec<MazeMazeGenerator<? extends ChunkGenerator>> getCodec() {
		return CODEC;
	}

}
