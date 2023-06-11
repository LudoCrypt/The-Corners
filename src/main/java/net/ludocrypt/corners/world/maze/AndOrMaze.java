package net.ludocrypt.corners.world.maze;

import net.ludocrypt.limlib.api.world.maze.MazeComponent;

public class AndOrMaze extends MazeComponent {

	MazeComponent[] components;

	public AndOrMaze(MazeComponent... components) {
		super(components[0].width, components[0].height);
		this.components = components;
	}

	@Override
	public void generateMaze() {
		for (MazeComponent maze : components) {
			for (Vec2i pos : maze.solvedMaze) {
				int x = pos.getX();
				int y = pos.getY();

				if (maze.cellState(x, y).isNorth()) {
					this.cellState(x, y).setNorth(true);
				}
				if (maze.cellState(x, y).isEast()) {
					this.cellState(x, y).setEast(true);
				}
				if (maze.cellState(x, y).isSouth()) {
					this.cellState(x, y).setSouth(true);
				}
				if (maze.cellState(x, y).isWest()) {
					this.cellState(x, y).setWest(true);
				}
				if (this.cellState(x, y).isNorth() || this.cellState(x, y).isEast() || this.cellState(x, y).isSouth() || this.cellState(x, y).isWest()) {
					this.solvedMaze.add(new Vec2i(x, y));
				}
				if (this.cellState(x, y).isNorth() && this.cellState(x, y).isEast() && this.cellState(x, y).isSouth() && this.cellState(x, y).isWest()) {
					continue;
				}
			}
		}
	}

}
