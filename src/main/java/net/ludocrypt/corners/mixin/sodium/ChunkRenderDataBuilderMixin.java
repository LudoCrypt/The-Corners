package net.ludocrypt.corners.mixin.sodium;

import java.util.HashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Maps;

import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderData;
import net.ludocrypt.corners.access.ContainsSkyboxBlocksAccess;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

@Mixin(ChunkRenderData.Builder.class)
public class ChunkRenderDataBuilderMixin implements ContainsSkyboxBlocksAccess {

	@Unique
	private final HashMap<BlockPos, BlockState> skyboxBlocks = Maps.newHashMap();

	@Inject(method = "Lme/jellysquid/mods/sodium/client/render/chunk/data/ChunkRenderData$Builder;build()Lme/jellysquid/mods/sodium/client/render/chunk/data/ChunkRenderData;", at = @At("RETURN"), cancellable = true, remap = false)
	private void corners$build(CallbackInfoReturnable<ChunkRenderData> ci) {
		ChunkRenderData data = ci.getReturnValue();
		((ContainsSkyboxBlocksAccess) data).getSkyboxBlocks().putAll(skyboxBlocks);
		ci.setReturnValue(data);
	}

	@Override
	public HashMap<BlockPos, BlockState> getSkyboxBlocks() {
		return skyboxBlocks;
	}

}
