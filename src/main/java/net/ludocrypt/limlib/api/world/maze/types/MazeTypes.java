package net.ludocrypt.limlib.api.world.maze.types;

import java.util.List;
import java.util.Stack;

import com.google.common.collect.Lists;

import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Face;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.minecraft.util.random.RandomGenerator;

public class MazeTypes {

	public static MazeComponent combine(MazeComponent... components) {
		MazeComponent combined = new MazeComponent(components[0].width, components[0].height);

		for (MazeComponent maze : components) {

			for (int x = 0; x < components[0].width; x++) {

				for (int y = 0; y < components[0].height; y++) {
					CellState reference = maze.cellState(x, y);

					for (Face face : Face.values()) {

						if (reference.goes(face)) {
							combined.cellState(x, y).go(face);
						}

					}

					combined.cellState(x, y).appendAll(reference.getExtra());
				}

			}

		}

		return combined;
	}

	public static MazeComponent dilate(MazeComponent mazeIn, int dilationX, int dilationY) {
		MazeComponent dilate = new MazeComponent(mazeIn.width * dilationX, mazeIn.height * dilationY);

		for (int x = 0; x < mazeIn.width; x++) {

			for (int y = 0; y < mazeIn.height; y++) {

				for (int dx = 0; dx < dilationX; dx++) {

					for (int dy = 0; dy < dilationY; dy++) {
						int mazeX = x * dilationX + dx;
						int mazeY = y * dilationY + dy;
						Vec2i position = new Vec2i(mazeX, mazeY);
						CellState reference = mazeIn.cellState(x, y);

						if (dx % dilationX == 0) {

							if (dy % dilationY == 0) {
								CellState copy = reference.copy();
								copy.setPosition(position);
								dilate.maze[mazeY * dilate.width + mazeX] = copy;
							} else {

								if (reference.goesRight()) {
									CellState copy = new CellState();
									copy.right();
									copy.left();
									copy.setPosition(position);
									copy.appendAll(reference.getExtra());
									dilate.maze[mazeY * dilate.width + mazeX] = copy;
								}

							}

						} else {

							if (dy % dilationY == 0) {

								if (reference.goesUp()) {
									CellState copy = new CellState();
									copy.up();
									copy.down();
									copy.setPosition(position);
									copy.appendAll(reference.getExtra());
									dilate.maze[mazeY * dilate.width + mazeX] = copy;
								}

							} else {
								CellState copy = new CellState();
								copy.setPosition(position);
								copy.appendAll(reference.getExtra());
								dilate.maze[mazeY * dilate.width + mazeX] = copy;
							}

						}

					}

				}

			}

		}

		return dilate;
	}

	public static MazeComponent depthFirst(int width, int height, RandomGenerator random) {
		DepthLikeMaze depthLike = new DepthLikeMaze(new MazeComponent(width, height));
		MazeComponent maze = depthLike.maze;

		Vec2i start = new Vec2i(random.nextInt(width), random.nextInt(height));

		depthLike.visit(start);
		depthLike.stack.push(start);

		depthLike.visitedCells++;

		while (depthLike.visitedCells < maze.width * maze.height) {
			List<Face> neighbours = Lists.newArrayList();

			for (Face face : Face.values()) {

				if (depthLike.hasNeighbour(depthLike.stack.peek(), face)) {
					neighbours.add(face);
				}

			}

			if (!neighbours.isEmpty()) {
				Face nextFace = neighbours.get(random.nextInt(neighbours.size()));

				maze.cellState(depthLike.stack.peek()).go(nextFace);
				maze.cellState(depthLike.stack.peek().go(nextFace)).go(nextFace.mirror());
				depthLike.visit(depthLike.stack.peek().go(nextFace));
				depthLike.stack.push(depthLike.stack.peek().go(nextFace));

				depthLike.visitedCells++;

			} else {
				depthLike.stack.pop();
			}

		}

		return maze;
	}

	public static MazeComponent solve(MazeComponent mazeToSolve, RandomGenerator random, Vec2i end, Vec2i... roots) {
		DepthLikeMaze depthLike = new DepthLikeMaze(new MazeComponent(mazeToSolve.width, mazeToSolve.height));
		MazeComponent maze = depthLike.maze;

		List<Vec2i> beginnings = Lists.newArrayList(roots);

		List<Stack<Vec2i>> paths = Lists.newArrayList();

		for (Vec2i beginning : beginnings) {
			Stack<Vec2i> stack = new Stack<Vec2i>();
			stack.push(new Vec2i(beginning.getX(), beginning.getY()));
			Vec2i peek = stack.peek();
			depthLike.visit(peek);

			while (!peek.equals(end)) {
				List<Face> neighbours = Lists.newArrayList();

				for (Face face : Face.values()) {

					if (depthLike.hasNeighbour(peek, face)) {
						neighbours.add(face);
					}

				}

				if (!neighbours.isEmpty()) {
					Face nextFace = neighbours.get(random.nextInt(neighbours.size()));

					depthLike.visit(peek.go(nextFace));
					stack.push(peek.go(nextFace));

				} else {
					stack.pop();
				}

				peek = stack.peek();
			}

			for (int x = 0; x < mazeToSolve.width; x++) {

				for (int y = 0; y < mazeToSolve.height; y++) {
					depthLike.visit(new Vec2i(x, y), false);
				}

			}

			paths.add(stack);
		}

		for (Stack<Vec2i> path : paths) {

			for (int i = 0; i < path.size(); i++) {
				Vec2i pos = path.get(i);

				if (i + 1 != path.size()) {
					Vec2i nextPos = path.get(i + 1);

					Face face = pos.normal(nextPos);
					maze.cellState(pos).go(face);
					maze.cellState(pos.go(face)).go(face.mirror());

					maze.cellState(pos).appendAll(mazeToSolve.cellState(pos).getExtra());

				}

				if (beginnings.contains(pos) || pos.equals(end)) {

					if (pos.getX() == 0) {
						maze.cellState(pos).down();
					}

					if (pos.getY() == 0) {
						maze.cellState(pos).left();
					}

					if (pos.getX() == maze.width - 1) {
						maze.cellState(pos).up();
					}

					if (pos.getY() == maze.height - 1) {
						maze.cellState(pos).right();
					}

				}

			}

		}

		return maze;
	}

}
