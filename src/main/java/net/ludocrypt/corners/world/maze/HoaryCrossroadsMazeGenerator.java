package net.ludocrypt.corners.world.maze;

import java.util.HashMap;

import net.ludocrypt.limlib.world.maze.MazeComponent;
import net.ludocrypt.limlib.world.maze.RectangularMazeGenerator;
import net.minecraft.util.math.BlockPos;

public class HoaryCrossroadsMazeGenerator extends RectangularMazeGenerator<MazeComponent> {

	public final HashMap<BlockPos, MazeComponent> grandMazeMap = new HashMap<BlockPos, MazeComponent>(30);
	public final int dilation;

	public HoaryCrossroadsMazeGenerator(int width, int height, int dilation, long seedModifier) {
		super(width * dilation, height * dilation, 8, false, seedModifier);
		this.dilation = dilation;
	}

	@Override
	public int mod(int x, int n) {
		return super.mod(x, n);
	}

	@Override
	public long blockSeed(long x, long y, long z) {
		return super.blockSeed(x, y, z);
	}

}
