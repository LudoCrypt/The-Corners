package net.ludocrypt.corners.world.maze;

import java.util.HashMap;

import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.ludocrypt.limlib.api.world.maze.MazeGenerator;
import net.minecraft.util.math.BlockPos;

public class GrandMazeGenerator extends MazeGenerator<MazeComponent> {

	public final HashMap<BlockPos, MazeComponent> grandMazeMap = new HashMap<BlockPos, MazeComponent>(30);
	public final int dilation;

	public GrandMazeGenerator(int width, int height, int dilation, long seedModifier) {
		super(width * dilation, height * dilation, 8, 8, seedModifier);
		this.dilation = dilation;
	}

}
