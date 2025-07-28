package net.ludocrypt.limlib.api.world;

import net.minecraft.util.math.BlockPos;

public class LimlibHelper {

	public static long blockSeed(BlockPos pos) {
		return blockSeed(pos.getX(), pos.getY(), pos.getZ());
	}

	public static long blockSeed(long x, long y, long z) {
		long l = (x * 3129871) ^ z * 116129781L ^ y;
		l = l * l * 42317861L + l * 11L;
		return l >> 16;
	}

}
