package net.ludocrypt.corners.world.maze;

import java.util.HashMap;

import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.ludocrypt.limlib.api.world.maze.RectangularMazeGenerator;
import net.minecraft.util.math.BlockPos;

public class GrandMazeGenerator extends RectangularMazeGenerator<MazeComponent> {

	public final HashMap<BlockPos, MazeComponent> grandMazeMap = new HashMap<BlockPos, MazeComponent>(30);
	public final int dilation;

	public GrandMazeGenerator(int width, int height, int dilation, long seedModifier) {
		super(width * dilation, height * dilation, 8, false, seedModifier);
		this.dilation = dilation;
	}

}
