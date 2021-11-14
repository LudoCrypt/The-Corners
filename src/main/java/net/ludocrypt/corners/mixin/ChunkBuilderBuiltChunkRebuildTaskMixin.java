package net.ludocrypt.corners.mixin;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.Lists;

import net.ludocrypt.corners.access.BlockRenderManagerAccess;
import net.ludocrypt.corners.access.ContainsSkyboxBlocksAccess;
import net.ludocrypt.corners.client.render.sky.SkyboxShaders;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Mixin(targets = "net.minecraft.client.render.chunk.ChunkBuilder$BuiltChunk$RebuildTask")
public class ChunkBuilderBuiltChunkRebuildTaskMixin {

	@Inject(method = "Lnet/minecraft/client/render/chunk/ChunkBuilder$BuiltChunk$RebuildTask;render(FFFLnet/minecraft/client/render/chunk/ChunkBuilder$ChunkData;Lnet/minecraft/client/render/chunk/BlockBufferBuilderStorage;)Ljava/util/Set;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/chunk/ChunkRendererRegion;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private void corners$render(float cameraX, float cameraY, float cameraZ, ChunkBuilder.ChunkData data, BlockBufferBuilderStorage buffers, CallbackInfoReturnable<Set<BlockEntity>> cir, int i, BlockPos blockPos, BlockPos blockPos2, ChunkOcclusionDataBuilder chunkOcclusionDataBuilder, Set<BlockEntity> set, ChunkRendererRegion chunkRendererRegion, MatrixStack matrixStack, Random random, BlockRenderManager blockRenderManager, Iterator<BlockPos> var15, BlockPos blockPos3, BlockState blockState) {
		List<BakedQuad> quads = Lists.newArrayList();
		BakedModel model = ((BlockRenderManagerAccess) blockRenderManager).getModelPure(blockState);
		SkyboxShaders.addAll(quads, model, blockState);
		for (Direction dir : Direction.values()) {
			SkyboxShaders.addAll(quads, model, blockState, dir);
		}
		if (!quads.isEmpty()) {
			((ContainsSkyboxBlocksAccess) data).getSkyboxBlocks().put(blockPos3.toImmutable(), blockState);
		}
	}

}
