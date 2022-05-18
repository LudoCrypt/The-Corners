package net.ludocrypt.corners.util;

import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.minecraft.util.math.BlockPos;

public class DistanceCellState extends CellState {

	private int distance;

	public DistanceCellState(CellState in) {
		this.setPosition(in.getPosition());
		this.setNorth(in.isNorth());
		this.setEast(in.isEast());
		this.setSouth(in.isSouth());
		this.setWest(in.isWest());
		this.setVisited(in.isVisited());
	}

	public static void quantizeMaze(MazeComponent maze) {
		DistanceCellState[] newCells = new DistanceCellState[maze.maze.length];

		for (int i = 0; i < newCells.length; i++) {
			newCells[i] = new DistanceCellState(maze.maze[i]);
		}

		for (DistanceCellState cell : newCells) {
			int shortestDistance = maze.width * maze.height;
			Iterable<BlockPos> iterable = BlockPos.iterateOutwards(new BlockPos(cell.getPosition().getX(), cell.getPosition().getY(), 0), maze.width, maze.height, 0);
			for (BlockPos pos : iterable) {
				if ((pos.getX() < maze.width) && (pos.getY() < maze.height) && (pos.getX() > 0) && (pos.getY() > 0)) {
					if (maze.cellState(pos.getX(), pos.getY()).isNorth() || maze.cellState(pos.getX(), pos.getY()).isEast() || maze.cellState(pos.getX(), pos.getY()).isSouth() || maze.cellState(pos.getX(), pos.getY()).isWest()) {
						int cellDistance = pos.getManhattanDistance(new BlockPos(cell.getPosition().getX(), cell.getPosition().getY(), 0));
						if (cellDistance < shortestDistance) {
							shortestDistance = cellDistance;
						}
					}
				}
				if (shortestDistance == 1) {
					break;
				}
			}
			cell.setDistance(shortestDistance);
			maze.maze[cell.getPosition().getY() * maze.width + cell.getPosition().getX()] = cell;
		}
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

}
