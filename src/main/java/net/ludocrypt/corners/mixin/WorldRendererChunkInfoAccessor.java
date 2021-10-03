package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;

@Mixin(WorldRenderer.ChunkInfo.class)
public interface WorldRendererChunkInfoAccessor {

	@Accessor("chunk")
	ChunkBuilder.BuiltChunk getChunk();

}
