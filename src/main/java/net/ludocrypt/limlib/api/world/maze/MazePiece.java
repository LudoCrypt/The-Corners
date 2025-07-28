package net.ludocrypt.limlib.api.world.maze;

import com.mojang.datafixers.util.Pair;

import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.minecraft.util.random.RandomGenerator;

public enum MazePiece {

	F("f"),
	L("l"),
	I("i"),
	N("n"),
	T("t"),
	E("e");

	final String asLetter;

	private MazePiece(String asLetter) {
		this.asLetter = asLetter;
	}

	public String getAsLetter() {
		return asLetter;
	}

	/**
	 * Returns a Pair containing the state's maze piece and a randomly selected
	 * manipulation.
	 * 
	 * Note: This method assumes a default orientation of each maze piece.
	 * 
	 * T is assumed to go up, left, down, right. F is assumed to go up, left, down.
	 * L is assumed to go up, left. I is assumed to go up, down. N is assumed to go
	 * up.
	 */
	public static Pair<MazePiece, Manipulation> getFromCell(CellState state, RandomGenerator random) {

		MazePiece piece = MazePiece.E;
		Manipulation[] options = new Manipulation[] { Manipulation.NONE };

		if (state.goesLeft() && state.goesUp() && state.goesRight() && state.goesDown()) {
			piece = T;
			options = new Manipulation[] { Manipulation.NONE, Manipulation.CLOCKWISE_90, Manipulation.CLOCKWISE_180,
				Manipulation.COUNTERCLOCKWISE_90, Manipulation.FRONT_BACK, Manipulation.LEFT_RIGHT,
				Manipulation.TOP_LEFT_BOTTOM_RIGHT, Manipulation.TOP_RIGHT_BOTTOM_LEFT };
		} else if (state.goesLeft() && state.goesUp() && state.goesRight() && !state.goesDown()) {
			piece = F;
			options = new Manipulation[] { Manipulation.CLOCKWISE_90, Manipulation.TOP_RIGHT_BOTTOM_LEFT };
		} else if (state.goesLeft() && state.goesUp() && !state.goesRight() && state.goesDown()) {
			piece = F;
			options = new Manipulation[] { Manipulation.NONE, Manipulation.FRONT_BACK };
		} else if (state.goesLeft() && state.goesUp() && !state.goesRight() && !state.goesDown()) {
			piece = L;
			options = new Manipulation[] { Manipulation.NONE, Manipulation.TOP_RIGHT_BOTTOM_LEFT };
		} else if (state.goesLeft() && !state.goesUp() && state.goesRight() && state.goesDown()) {
			piece = F;
			options = new Manipulation[] { Manipulation.COUNTERCLOCKWISE_90, Manipulation.TOP_LEFT_BOTTOM_RIGHT };
		} else if (state.goesLeft() && !state.goesUp() && state.goesRight() && !state.goesDown()) {
			piece = I;
			options = new Manipulation[] { Manipulation.CLOCKWISE_90, Manipulation.COUNTERCLOCKWISE_90,
				Manipulation.TOP_LEFT_BOTTOM_RIGHT, Manipulation.TOP_RIGHT_BOTTOM_LEFT };
		} else if (state.goesLeft() && !state.goesUp() && !state.goesRight() && state.goesDown()) {
			piece = L;
			options = new Manipulation[] { Manipulation.COUNTERCLOCKWISE_90, Manipulation.FRONT_BACK };
		} else if (state.goesLeft() && !state.goesUp() && !state.goesRight() && !state.goesDown()) {
			piece = N;
			options = new Manipulation[] { Manipulation.COUNTERCLOCKWISE_90, Manipulation.TOP_RIGHT_BOTTOM_LEFT };
		} else if (!state.goesLeft() && state.goesUp() && state.goesRight() && state.goesDown()) {
			piece = F;
			options = new Manipulation[] { Manipulation.CLOCKWISE_180, Manipulation.LEFT_RIGHT };
		} else if (!state.goesLeft() && state.goesUp() && state.goesRight() && !state.goesDown()) {
			piece = L;
			options = new Manipulation[] { Manipulation.CLOCKWISE_90, Manipulation.LEFT_RIGHT };
		} else if (!state.goesLeft() && state.goesUp() && !state.goesRight() && state.goesDown()) {
			piece = I;
			options = new Manipulation[] { Manipulation.NONE, Manipulation.CLOCKWISE_180, Manipulation.FRONT_BACK,
				Manipulation.LEFT_RIGHT };
		} else if (!state.goesLeft() && state.goesUp() && !state.goesRight() && !state.goesDown()) {
			piece = N;
			options = new Manipulation[] { Manipulation.NONE, Manipulation.LEFT_RIGHT };
		} else if (!state.goesLeft() && !state.goesUp() && state.goesRight() && state.goesDown()) {
			piece = L;
			options = new Manipulation[] { Manipulation.CLOCKWISE_180, Manipulation.TOP_LEFT_BOTTOM_RIGHT };
		} else if (!state.goesLeft() && !state.goesUp() && state.goesRight() && !state.goesDown()) {
			piece = N;
			options = new Manipulation[] { Manipulation.CLOCKWISE_90, Manipulation.TOP_LEFT_BOTTOM_RIGHT };
		} else if (!state.goesLeft() && !state.goesUp() && !state.goesRight() && state.goesDown()) {
			piece = N;
			options = new Manipulation[] { Manipulation.CLOCKWISE_180, Manipulation.FRONT_BACK };
		}

		return Pair.of(piece, options[random.nextInt(options.length)]);

	}

}
