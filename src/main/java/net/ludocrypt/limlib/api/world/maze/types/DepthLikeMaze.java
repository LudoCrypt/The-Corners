package net.ludocrypt.limlib.api.world.maze.types;

import java.util.Stack;

import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Face;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.minecraft.nbt.NbtCompound;

public final class DepthLikeMaze {

	public MazeComponent maze;
	public Stack<Vec2i> stack = new Stack<Vec2i>();

	public int visitedCells = 0;

	public DepthLikeMaze(MazeComponent maze) {
		this.maze = maze;
	}

	public boolean hasNeighbourUp(Vec2i vec) {
		return this.maze.fits(vec.up()) && !visited(vec.up());
	}

	public boolean hasNeighbourRight(Vec2i vec) {
		return this.maze.fits(vec.right()) && !visited(vec.right());
	}

	public boolean hasNeighbourDown(Vec2i vec) {
		return this.maze.fits(vec.down()) && !visited(vec.down());
	}

	public boolean hasNeighbourLeft(Vec2i vec) {
		return this.maze.fits(vec.left()) && !visited(vec.left());
	}

	public boolean hasNeighbours(Vec2i vec) {
		return this.hasNeighbourUp(vec) || this.hasNeighbourRight(vec) || this.hasNeighbourDown(vec) || this
			.hasNeighbourLeft(vec);
	}

	public boolean hasNeighbour(Vec2i vec, Face face) {
		return switch (face) {
			case UP -> hasNeighbourUp(vec);
			case DOWN -> hasNeighbourDown(vec);
			case LEFT -> hasNeighbourLeft(vec);
			case RIGHT -> hasNeighbourRight(vec);
		};
	}

	public NbtCompound visit(Vec2i vec) {
		return visit(vec, true);
	}

	public NbtCompound visit(Vec2i vec, boolean visited) {
		NbtCompound appendage = new NbtCompound();
		appendage.putBoolean("visited", visited);
		this.maze.cellState(vec).getExtra().put("visited", appendage);
		return appendage;
	}

	public boolean visited(Vec2i vec) {
		return this.maze.cellState(vec).getExtra().containsKey("visited")
				? this.maze.cellState(vec).getExtra().get("visited").getBoolean("visited")
				: false;
	}

}
