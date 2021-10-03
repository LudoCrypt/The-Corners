package net.ludocrypt.corners.access;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;

public interface BlockRenderManagerAccess {

	public BakedModel getModelPure(BlockState state);

}
