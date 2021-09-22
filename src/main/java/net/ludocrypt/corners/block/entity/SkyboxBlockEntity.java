package net.ludocrypt.corners.block.entity;

import net.ludocrypt.corners.init.CornerBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SkyboxBlockEntity extends BlockEntity {

	public SkyboxBlockEntity(BlockPos pos, BlockState state) {
		super(CornerBlocks.SKYBOX_BLOCK_ENTITY, pos, state);
	}

}
