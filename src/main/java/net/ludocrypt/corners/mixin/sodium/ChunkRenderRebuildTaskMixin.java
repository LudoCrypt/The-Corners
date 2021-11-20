package net.ludocrypt.corners.mixin.sodium;

import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.Lists;

import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildResult;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderBounds;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderData;
import me.jellysquid.mods.sodium.client.render.chunk.tasks.ChunkRenderRebuildTask;
import me.jellysquid.mods.sodium.client.render.pipeline.context.ChunkRenderCacheLocal;
import me.jellysquid.mods.sodium.client.util.task.CancellationSource;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.ludocrypt.corners.access.ContainsSkyboxBlocksAccess;
import net.ludocrypt.corners.client.render.sky.SkyboxShaders;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Mixin(ChunkRenderRebuildTask.class)
public class ChunkRenderRebuildTaskMixin {

	@Inject(method = "Lme/jellysquid/mods/sodium/client/render/chunk/tasks/ChunkRenderRebuildTask;performBuild(Lme/jellysquid/mods/sodium/client/render/pipeline/context/ChunkRenderCacheLocal;Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildBuffers;Lme/jellysquid/mods/sodium/client/util/task/CancellationSource;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildResult;", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/pipeline/BlockRenderer;renderModel(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/render/model/BakedModel;Lme/jellysquid/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;ZJ)Z", shift = Shift.BEFORE, ordinal = 0, remap = false), locals = LocalCapture.CAPTURE_FAILEXCEPTION, remap = false)
	private void corners$performBuild(ChunkRenderCacheLocal cache, ChunkBuildBuffers buffers, CancellationSource cancellationSource, CallbackInfoReturnable<ChunkBuildResult> ci, ChunkRenderData.Builder renderData, ChunkOcclusionDataBuilder occluder, ChunkRenderBounds.Builder bounds, WorldSlice slice, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockPos.Mutable blockPos, BlockPos.Mutable offset, int y, int z, int x, BlockState blockState, boolean rendered, RenderLayer layer, BakedModel model, long seed) {
		List<BakedQuad> quads = Lists.newArrayList();
		SkyboxShaders.addAll(quads, model, blockState, new Random(seed));
		for (Direction dir : Direction.values()) {
			SkyboxShaders.addAll(quads, model, blockState, dir, new Random(seed));
		}
		if (!quads.isEmpty()) {
			((ContainsSkyboxBlocksAccess) renderData).getSkyboxBlocks().put(blockPos.toImmutable(), blockState);
		}
	}

}
