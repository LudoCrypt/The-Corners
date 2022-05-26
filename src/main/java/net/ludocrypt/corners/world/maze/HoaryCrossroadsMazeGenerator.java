package net.ludocrypt.corners.world.maze;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.ibm.icu.impl.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.limlib.api.world.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.DepthFirstMaze;
import net.ludocrypt.limlib.api.world.maze.DepthFirstMazeSolver;
import net.ludocrypt.limlib.api.world.maze.DilateMaze;
import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.ludocrypt.limlib.api.world.maze.MazeGenerator;
import net.ludocrypt.limlib.api.world.maze.MazePiece;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class HoaryCrossroadsMazeGenerator extends MazeGenerator<AbstractNbtChunkGenerator, MazeComponent> {

	public static final Codec<HoaryCrossroadsMazeGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.INT.fieldOf("width").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.width;
		}), Codec.INT.fieldOf("height").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.height;
		}), Codec.INT.fieldOf("dilation").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.dilation;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.seedModifier;
		})).apply(instance, instance.stable(HoaryCrossroadsMazeGenerator::new));
	});

	private final HashMap<BlockPos, MazeComponent> grandMazeMap = new HashMap<BlockPos, MazeComponent>(30);

	private int dilation;

	public HoaryCrossroadsMazeGenerator(int width, int height, int dilation, long seedModifier) {
		super(width * dilation, height * dilation, 8, false, seedModifier);
		this.dilation = dilation;
	}

	@Override
	public MazeComponent newMaze(BlockPos mazePos, ChunkRegion region, Chunk chunk, AbstractNbtChunkGenerator chunkGenerator, int realWidth, int realHeight, Random random) {
		BlockPos grandMazePos = new BlockPos(mazePos.getX() - mod(mazePos.getX(), (width * width * thickness)), 0, mazePos.getZ() - mod(mazePos.getZ(), (height * height * thickness)));

		MazeComponent grandMaze;
		if (this.grandMazeMap.containsKey(grandMazePos)) {
			grandMaze = this.grandMazeMap.get(grandMazePos);
		} else {
			grandMaze = new DepthFirstMaze(width / dilation, height / dilation, new Random(blockSeed(grandMazePos.getX(), this.seedModifier, grandMazePos.getZ())));
			grandMaze.generateMaze();
			this.grandMazeMap.put(grandMazePos, grandMaze);
		}

		CellState originCell = grandMaze.cellState((((mazePos.getX() - grandMazePos.getX()) / thickness) / width) / dilation, (((mazePos.getZ() - grandMazePos.getZ()) / thickness) / height) / dilation);

		Vec2i start = null;
		List<Vec2i> endings = Lists.newArrayList();

		if (originCell.isSouth() || originCell.getPosition().getX() == 0) {
			if (start == null) {
				start = new Vec2i(0, (height / dilation) / 2);
			}
		}

		if (originCell.isWest() || originCell.getPosition().getY() == 0) {
			if (start == null) {
				start = new Vec2i((width / dilation) / 2, 0);
			} else {
				endings.add(new Vec2i((width / dilation) / 2, 0));
			}
		}

		if (originCell.isNorth() || originCell.getPosition().getX() == (width / dilation) - 1) {
			if (start == null) {
				start = new Vec2i((width / dilation) - 1, (height / dilation) / 2);
			} else {
				endings.add(new Vec2i((width / dilation) - 1, (height / dilation) / 2));
			}
		}

		if (originCell.isEast() || originCell.getPosition().getY() == (height / dilation) - 1) {
			if (start == null) {
				start = new Vec2i((width / dilation) / 2, (height / dilation) - 1);
			} else {
				endings.add(new Vec2i((width / dilation) / 2, (height / dilation) - 1));
			}
		}

		// Allow Grand Nubs
		if (endings.isEmpty()) {
			endings.add(new Vec2i(random.nextInt((width / dilation) - 2) + 1, random.nextInt((height / dilation) - 2) + 1));
		}

		MazeComponent mazeToSolve = new DepthFirstMaze(width / dilation, height / dilation, random);
		mazeToSolve.generateMaze();
		DepthFirstMazeSolver solvedMaze = new DepthFirstMazeSolver(mazeToSolve, start, endings, random);
		solvedMaze.generateMaze();

		MazeComponent maze = new DilateMaze(solvedMaze, dilation);
		maze.generateMaze();

		return maze;
	}

	@Override
	public void decorateCell(BlockPos pos, BlockPos mazePos, Chunk chunk, ChunkRegion region, AbstractNbtChunkGenerator chunkGenerator, MazeComponent maze, CellState state, int thickness) {
		Random random = new Random(blockSeed(pos.getX(), blockSeed(mazePos.getZ(), region.getSeed(), mazePos.getX()), pos.getZ()));
		Pair<MazePiece, BlockRotation> mazeSegment = MazePiece.getFromCell(state, random);
		if (mazeSegment.first != MazePiece.BLANK) {
			placeNbt(getPiece(mazeSegment.first, random), getPieceAsBottom(mazeSegment.first, random), chunkGenerator, region, pos, mazeSegment.second);
		} else if (random.nextInt(67) == 0) {
			BlockPos offset = pos.add(random.nextInt(7), 0, random.nextInt(7));
			chunkGenerator.generateNbt(region, offset.add(0, 264, 0), "hoary_crossroads_obelisk_" + random.nextInt(5), BlockRotation.random(random));
			for (int i = 0; i < 264; i++) {
				region.setBlockState(offset.add(0, i, 0), Blocks.POLISHED_DEEPSLATE.getDefaultState(), Block.FORCE_STATE);
				region.setBlockState(offset.add(1, i, 0), Blocks.POLISHED_DEEPSLATE.getDefaultState(), Block.FORCE_STATE);
				region.setBlockState(offset.add(1, i, 1), Blocks.POLISHED_DEEPSLATE.getDefaultState(), Block.FORCE_STATE);
				region.setBlockState(offset.add(0, i, 1), Blocks.POLISHED_DEEPSLATE.getDefaultState(), Block.FORCE_STATE);
			}
		}
	}

	public String getPiece(MazePiece piece, Random random) {
		switch (piece) {
		case F_PIECE:
			return "hoary_crossroads_f_" + ((random.nextInt(12) == 0) ? ((random.nextInt(8) == 0) ? "rare_0" : random.nextInt(8)) : "0");
		case I_PIECE:
			return "hoary_crossroads_i_" + ((random.nextInt(12) == 0) ? ((random.nextInt(8) == 0) ? "rare_0" : random.nextInt(10)) : "0");
		case L_PIECE:
			return "hoary_crossroads_l_" + ((random.nextInt(12) == 0) ? ((random.nextInt(8) == 0) ? "rare_0" : random.nextInt(10)) : "0");
		case NUB:
			return "hoary_crossroads_nub_" + ((random.nextInt(12) == 0) ? random.nextInt(8) : "0");
		case T_PIECE:
			return "hoary_crossroads_t_" + ((random.nextInt(12) == 0) ? ((random.nextInt(8) == 0) ? ("rare_" + random.nextInt(1)) : random.nextInt(8)) : "0");
		default:
			return "hoary_crossroads_nub_0";
		}
	}

	public String getPieceAsBottom(MazePiece piece, Random random) {
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

	private void placeNbt(String nbt, String bottomNbt, AbstractNbtChunkGenerator chunkGenerator, ChunkRegion region, BlockPos basePos, BlockRotation rotation) {
		chunkGenerator.generateNbt(region, basePos.up(256), nbt, rotation);
		for (int i = 0; i < 256; i++) {
			chunkGenerator.generateNbt(region, basePos.up(i), bottomNbt, rotation);
		}
	}

	@Override
	public Codec<? extends MazeGenerator<? extends ChunkGenerator, ? extends MazeComponent>> getCodec() {
		return CODEC;
	}

}
