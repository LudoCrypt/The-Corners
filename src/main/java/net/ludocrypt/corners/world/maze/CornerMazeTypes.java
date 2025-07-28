package net.ludocrypt.corners.world.maze;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.Lists;

import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Face;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.ludocrypt.limlib.api.world.maze.types.DepthLikeMaze;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.random.RandomGenerator;

public class CornerMazeTypes {

	public static MazeComponent biasDepthFirst(int width, int height, RandomGenerator random, double bias) {
		DepthLikeMaze depthLike = new DepthLikeMaze(new MazeComponent(width, height));
		MazeComponent maze = depthLike.maze;

		BiFunction<Vec2i, Face, NbtCompound> setDir = (vec, dir) -> {
			NbtCompound appendage = new NbtCompound();
			appendage.putByte("dir", (byte) dir.ordinal());
			maze.cellState(vec).getExtra().put("dir", appendage);
			return appendage;
		};

		Function<Vec2i, Face> getDir = (vec) -> {
			return maze.cellState(vec).getExtra().containsKey("dir")
					? Face.values()[maze.cellState(vec).getExtra().get("dir").getByte("dir")]
					: null;
		};

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

				if (getDir.apply(depthLike.stack.peek()) != null) {

					if (neighbours.contains(getDir.apply(depthLike.stack.peek()))) {

						if (random.nextDouble() < bias) {
							nextFace = getDir.apply(depthLike.stack.peek());
						}

					}

				}

				maze.cellState(depthLike.stack.peek()).go(nextFace);
				maze.cellState(depthLike.stack.peek().go(nextFace)).go(nextFace.mirror());
				depthLike.visit(depthLike.stack.peek().go(nextFace));
				depthLike.stack.push(depthLike.stack.peek().go(nextFace));
				setDir.apply(depthLike.stack.peek(), nextFace);

				depthLike.visitedCells++;

			} else {
				depthLike.stack.pop();
			}

		}

		return maze;
	}

}
