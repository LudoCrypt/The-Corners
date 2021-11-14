package net.ludocrypt.corners.mixin.sodium;

import java.util.HashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.google.common.collect.Maps;

import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderData;
import net.ludocrypt.corners.access.ContainsSkyboxBlocksAccess;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

@Mixin(ChunkRenderData.class)
public class ChunkRenderDataMixin implements ContainsSkyboxBlocksAccess {

	@Unique
	private final HashMap<BlockPos, BlockState> skyboxBlocks = Maps.newHashMap();

	@Override
	public HashMap<BlockPos, BlockState> getSkyboxBlocks() {
		return skyboxBlocks;
	}

}
