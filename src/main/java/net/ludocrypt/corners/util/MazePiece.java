package net.ludocrypt.corners.util;

import java.util.Random;

import com.ibm.icu.impl.Pair;

import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.minecraft.util.BlockRotation;

public enum MazePiece {
	T_PIECE, F_PIECE, I_PIECE, L_PIECE, NUB, BLANK;

	public static Pair<MazePiece, BlockRotation> getFromCell(CellState state, Random random) {

		MazePiece piece = MazePiece.BLANK;
		BlockRotation rotation = BlockRotation.NONE;

		if (state.isNorth() && state.isEast() && state.isSouth() && state.isWest()) {
			piece = MazePiece.T_PIECE;
			rotation = random.nextBoolean() ? BlockRotation.NONE : random.nextBoolean() ? random.nextBoolean() ? BlockRotation.CLOCKWISE_90 : BlockRotation.CLOCKWISE_180 : BlockRotation.COUNTERCLOCKWISE_90;
		} else if (state.isNorth() && state.isEast() && state.isSouth() && !state.isWest()) {
			piece = MazePiece.F_PIECE;
			rotation = BlockRotation.CLOCKWISE_180;
		} else if (state.isNorth() && state.isEast() && !state.isSouth() && state.isWest()) {
			piece = MazePiece.F_PIECE;
			rotation = BlockRotation.CLOCKWISE_90;
		} else if (state.isNorth() && state.isEast() && !state.isSouth() && !state.isWest()) {
			piece = MazePiece.L_PIECE;
			rotation = BlockRotation.CLOCKWISE_90;
		} else if (state.isNorth() && !state.isEast() && state.isSouth() && state.isWest()) {
			piece = MazePiece.F_PIECE;
		} else if (state.isNorth() && !state.isEast() && state.isSouth() && !state.isWest()) {
			piece = MazePiece.I_PIECE;
			rotation = random.nextBoolean() ? BlockRotation.NONE : BlockRotation.CLOCKWISE_180;
		} else if (state.isNorth() && !state.isEast() && !state.isSouth() && state.isWest()) {
			piece = MazePiece.L_PIECE;
		} else if (state.isNorth() && !state.isEast() && !state.isSouth() && !state.isWest()) {
			piece = MazePiece.NUB;
		} else if (!state.isNorth() && state.isEast() && state.isSouth() && state.isWest()) {
			piece = MazePiece.F_PIECE;
			rotation = BlockRotation.COUNTERCLOCKWISE_90;
		} else if (!state.isNorth() && state.isEast() && state.isSouth() && !state.isWest()) {
			piece = MazePiece.L_PIECE;
			rotation = BlockRotation.CLOCKWISE_180;
		} else if (!state.isNorth() && state.isEast() && !state.isSouth() && state.isWest()) {
			piece = MazePiece.I_PIECE;
			rotation = random.nextBoolean() ? BlockRotation.CLOCKWISE_90 : BlockRotation.COUNTERCLOCKWISE_90;
		} else if (!state.isNorth() && state.isEast() && !state.isSouth() && !state.isWest()) {
			piece = MazePiece.NUB;
			rotation = BlockRotation.CLOCKWISE_90;
		} else if (!state.isNorth() && !state.isEast() && state.isSouth() && state.isWest()) {
			piece = MazePiece.L_PIECE;
			rotation = BlockRotation.COUNTERCLOCKWISE_90;
		} else if (!state.isNorth() && !state.isEast() && state.isSouth() && !state.isWest()) {
			piece = MazePiece.NUB;
			rotation = BlockRotation.CLOCKWISE_180;
		} else if (!state.isNorth() && !state.isEast() && !state.isSouth() && state.isWest()) {
			piece = MazePiece.NUB;
		}

		return Pair.of(piece, rotation);

	}

}
