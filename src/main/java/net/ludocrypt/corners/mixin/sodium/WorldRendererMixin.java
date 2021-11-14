package net.ludocrypt.corners.mixin.sodium;

import java.util.HashMap;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import me.jellysquid.mods.sodium.client.world.WorldRendererExtended;
import net.ludocrypt.corners.access.ContainsSkyboxBlocksAccess;
import net.ludocrypt.corners.access.SodiumWorldRendererAccess;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin implements SodiumWorldRendererAccess {

	public List<Pair<BlockPos, BlockState>> getSodiumSkyboxModelPairs() {
		List<Pair<BlockPos, BlockState>> list = Lists.newArrayList();
		HashMap<BlockPos, BlockState> map = ((ContainsSkyboxBlocksAccess) ((WorldRendererExtended) (WorldRenderer) (Object) this).getSodiumWorldRenderer()).getSkyboxBlocks();
		map.forEach((pos, state) -> list.add(Pair.of(pos, state)));
		return list;
	}

}
