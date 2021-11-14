package net.ludocrypt.corners.access;

import java.util.HashMap;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface ContainsSkyboxBlocksAccess {

	public HashMap<BlockPos, BlockState> getSkyboxBlocks();

}
