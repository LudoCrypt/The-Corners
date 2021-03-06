package net.ludocrypt.corners.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class SkyboxGlassPaneBlock extends CornerPaneBlock {

	public SkyboxGlassPaneBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return super.getOutlineShape(state, world, pos, context);
	}

}
