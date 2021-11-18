package net.ludocrypt.corners.mixin.sodium;

import java.util.HashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Maps;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderData;
import net.ludocrypt.corners.access.ContainsSkyboxBlocksAccess;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

@Mixin(SodiumWorldRenderer.class)
public class SodiumWorldRendererMixin implements ContainsSkyboxBlocksAccess {

	@Unique
	private final HashMap<BlockPos, BlockState> skyboxBlocks = Maps.newHashMap();

	@Inject(method = "Lme/jellysquid/mods/sodium/client/render/SodiumWorldRenderer;unloadWorld()V", at = @At("TAIL"), remap = false)
	private void corners$unloadWorld(CallbackInfo ci) {
		this.skyboxBlocks.clear();
	}

	@Inject(method = "Lme/jellysquid/mods/sodium/client/render/SodiumWorldRenderer;onChunkRenderUpdated(IIILme/jellysquid/mods/sodium/client/render/chunk/data/ChunkRenderData;Lme/jellysquid/mods/sodium/client/render/chunk/data/ChunkRenderData;)V", at = @At("TAIL"), remap = false)
	private void corners$onChunkRenderUpdated(int x, int y, int z, ChunkRenderData before, ChunkRenderData after, CallbackInfo ci) {
		((ContainsSkyboxBlocksAccess) before).getSkyboxBlocks().keySet().forEach(skyboxBlocks::remove);
		skyboxBlocks.putAll(((ContainsSkyboxBlocksAccess) after).getSkyboxBlocks());
	}

	@Override
	public HashMap<BlockPos, BlockState> getSkyboxBlocks() {
		return skyboxBlocks;
	}

}
