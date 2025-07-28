package net.ludocrypt.limlib.api.world.maze;

import java.util.HashMap;
import java.util.function.BiConsumer;

import net.ludocrypt.limlib.api.world.LimlibHelper;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.ChunkRegion;

public class MazeGenerator {

	private final HashMap<Vec2i, MazeComponent> mazes = new HashMap<Vec2i, MazeComponent>(30);
	public final int width;
	public final int height;
	public final int thicknessX;
	public final int thicknessY;
	public final long seedModifier;

	private BiConsumer<Vec2i, MazeComponent> whenNewMaze;

	/**
	 * Creates a rectangular maze generator.
	 * <p>
	 * 
	 * @param width        of the maze
	 * @param height       of the maze
	 * @param thicknessX   of the cells in real world coordinates.
	 * @param thicknessY   of the cells in real world coordinates.
	 * @param seedModifier is the change to the seed when generating a new random
	 *                     maze. In code, it uses the world seed + seedModifier
	 */
	public MazeGenerator(int width, int height, int thicknessX, int thicknessY, long seedModifier) {
		this.width = width;
		this.height = height;
		this.thicknessX = thicknessX;
		this.thicknessY = thicknessY;
		this.seedModifier = seedModifier;
	}

	/**
	 * Begins generating a maze starting at pos. This should be run in every chunk
	 * with the pos being the beginning position of the chunk.
	 * 
	 * @param pos           the starting position for the maze logic to work.
	 * @param seed          the world seed
	 * @param mazeCreator   functional interface to create a new maze at a position
	 * @param cellDecorator funcional interface to generate a single maze block, or
	 *                      'cell'
	 */
	public void generateMaze(Vec2i pos, ChunkRegion region, MazeCreator mazeCreator, CellDecorator cellDecorator) {

		for (int x = 0; x < 16; x++) {

			for (int y = 0; y < 16; y++) {
				Vec2i inPos = pos.add(x, y);

				if (Math.floorMod(inPos.getX(), thicknessX) == 0 && Math.floorMod(inPos.getY(), thicknessY) == 0) {
					Vec2i realPos = getRealMaze(inPos);
					Vec2i mazePos = getInlineMaze(inPos);

					MazeComponent maze;

					if (this.mazes.containsKey(mazePos)) {
						maze = this.mazes.get(mazePos);
					} else {
						maze = mazeCreator
							.newMaze(region, realPos, new Vec2i(width, height),
								RandomGenerator
									.createLegacy(LimlibHelper
										.blockSeed(mazePos.getX(), mazePos.getY(), region.getSeed() + seedModifier)));
						this.mazes.put(mazePos, maze);

						if (this.whenNewMaze != null) {
							this.whenNewMaze.accept(mazePos, maze);
						}

					}

					CellState originCell = maze.cellState(getInlineCell(inPos));
					cellDecorator
						.generate(region, inPos, realPos, maze, originCell, new Vec2i(this.thicknessX, this.thicknessY),
							RandomGenerator
								.createLegacy(LimlibHelper
									.blockSeed(realPos.getX(), realPos.getY(), region.getSeed() + seedModifier)));
				}

			}

		}

	}

	public Vec2i getRealMaze(Vec2i real) {
		return new Vec2i(real.getX() - Math.floorMod(real.getX(), (width * thicknessX)),
			real.getY() - Math.floorMod(real.getY(), (height * thicknessY)));
	}

	public Vec2i getInlineMaze(Vec2i real) {
		Vec2i realMaze = getRealMaze(real);
		return new Vec2i(realMaze.getX() / (width * thicknessX), realMaze.getY() / (height * thicknessY));
	}

	public Vec2i getInlineCell(Vec2i real) {
		Vec2i realPos = getRealMaze(real);
		return new Vec2i((real.getX() - realPos.getX()) / thicknessX, (real.getY() - realPos.getY()) / thicknessY);
	}

	public HashMap<Vec2i, MazeComponent> getMazes() {
		return mazes;
	}

	public void connect(BiConsumer<Vec2i, MazeComponent> whenNewMaze) {
		this.whenNewMaze = whenNewMaze;
	}

	@FunctionalInterface
	public static interface CellDecorator {

		void generate(ChunkRegion region, Vec2i pos, Vec2i mazePos, MazeComponent maze, CellState state, Vec2i thickness,
				RandomGenerator random);

	}

	@FunctionalInterface
	public static interface MazeCreator {

		MazeComponent newMaze(ChunkRegion region, Vec2i mazePos, Vec2i size, RandomGenerator random);

	}

}
