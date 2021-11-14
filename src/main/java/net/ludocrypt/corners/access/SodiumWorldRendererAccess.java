package net.ludocrypt.corners.access;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface SodiumWorldRendererAccess {

	public List<Pair<BlockPos, BlockState>> getSodiumSkyboxModelPairs();

}
