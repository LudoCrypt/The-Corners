package net.ludocrypt.corners.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class ThinPillarBlock extends PillarBlock {

	protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
	protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 16.0D);
	protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D);

	public ThinPillarBlock(Settings settings) {
		super(settings);
	}

	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {

		switch (state.get(AXIS)) {
		case X:
		default:
			return X_SHAPE;
		case Z:
			return Z_SHAPE;
		case Y:
			return Y_SHAPE;
		}

	}

}
