package net.ludocrypt.corners.world.maze;

import java.util.List;
import java.util.Stack;

import com.google.common.collect.Lists;

import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;

public class StraightDepthFirstMaze extends MazeComponent {

	public Stack<NormalVec2i> stack = new Stack<NormalVec2i>();
	public RandomGenerator random;
	public double bias;

	public StraightDepthFirstMaze(int width, int height, RandomGenerator RandomGenerator, double bias) {
		super(width, height);
		this.random = RandomGenerator;
		this.bias = bias;
	}

	@Override
	public void generateMaze() {
		this.maze[0].visited();
		this.stack.push(new NormalVec2i(0, 0, -1));

		while (visitedCells < this.width * this.height) {
			List<Integer> neighbours = Lists.newArrayList();

			// North Neighbour
			if (this.hasNorthNeighbor(this.stack.peek())) {
				neighbours.add(0);
			}

			// East Neighbour
			if (this.hasEastNeighbor(this.stack.peek())) {
				neighbours.add(1);
			}

			// South Neighbour
			if (this.hasSouthNeighbor(this.stack.peek())) {
				neighbours.add(2);
			}

			// West Neighbour
			if (this.hasWestNeighbor(this.stack.peek())) {
				neighbours.add(3);
			}

			// Neighbour check
			if (!neighbours.isEmpty()) {
				int nextCellDir;

				if (random.nextDouble() < bias) {
					nextCellDir = this.stack.peek().getDir();

					if ((!neighbours.contains(nextCellDir)) || (nextCellDir == -1)) {
						nextCellDir = neighbours.get(random.nextInt(neighbours.size()));
					}

				} else {
					nextCellDir = neighbours.get(random.nextInt(neighbours.size()));
				}

				switch (nextCellDir) {
				case 0: // North
					this.cellState(this.stack.peek().getX(), this.stack.peek().getY()).north();
					this.cellState(this.stack.peek().getX() + 1, this.stack.peek().getY()).south();
					this.cellState(this.stack.peek().getX() + 1, this.stack.peek().getY()).visited();
					this.stack.push(new NormalVec2i(this.stack.peek().getX() + 1, this.stack.peek().getY(), 0));
					break;
				case 1: // East
					this.cellState(this.stack.peek().getX(), this.stack.peek().getY()).east();
					this.cellState(this.stack.peek().getX(), this.stack.peek().getY() + 1).west();
					this.cellState(this.stack.peek().getX(), this.stack.peek().getY() + 1).visited();
					this.stack.push(new NormalVec2i(this.stack.peek().getX(), this.stack.peek().getY() + 1, 1));
					break;
				case 2: // South
					this.cellState(this.stack.peek().getX(), this.stack.peek().getY()).south();
					this.cellState(this.stack.peek().getX() - 1, this.stack.peek().getY()).north();
					this.cellState(this.stack.peek().getX() - 1, this.stack.peek().getY()).visited();
					this.stack.push(new NormalVec2i(this.stack.peek().getX() - 1, this.stack.peek().getY(), 2));
					break;
				case 3: // West
					this.cellState(this.stack.peek().getX(), this.stack.peek().getY()).west();
					this.cellState(this.stack.peek().getX(), this.stack.peek().getY() - 1).east();
					this.cellState(this.stack.peek().getX(), this.stack.peek().getY() - 1).visited();
					this.stack.push(new NormalVec2i(this.stack.peek().getX(), this.stack.peek().getY() - 1, 3));
					break;
				}

				if (!this.solvedMaze.contains(new Vec2i(this.stack.peek().getX(), this.stack.peek().getY()))) {
					this.solvedMaze.add(new Vec2i(this.stack.peek().getX(), this.stack.peek().getY()));
				}

				// Visit Cell
				this.visitedCells++;
			} else {
				// Backtrack
				this.stack.pop();
			}

		}

	}

	public static class NormalVec2i extends Vec2i {

		private int dir;

		public NormalVec2i(int x, int y, int dir) {
			super(x, y);
			this.dir = dir;
		}

		public int getDir() {
			return dir;
		}

		public BlockPos toPos() {
			return new BlockPos(getX(), getY(), 0);
		}

		@Override
		public boolean equals(Object obj) {

			if (obj instanceof NormalVec2i pos) {
				return pos.getX() == this.getX() && pos.getY() == this.getY() && pos.dir == this.dir;
			}

			return super.equals(obj);
		}

		@Override
		public String toString() {
			return "(" + this.getX() + ", " + this.getY() + this.dir + ")";
		}

	}

}
