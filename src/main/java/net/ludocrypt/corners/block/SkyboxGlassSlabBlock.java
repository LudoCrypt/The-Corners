package net.ludocrypt.corners.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class SkyboxGlassSlabBlock extends SlabBlock {

	public SkyboxGlassSlabBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return super.getOutlineShape(state, world, pos, context);
	}

	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		if (stateFrom.isOf(this)) {
			if (state.get(TYPE).equals(SlabType.TOP)) {
				return stateFrom.get(TYPE).equals(SlabType.TOP) || stateFrom.get(TYPE).equals(SlabType.DOUBLE);
			} else if (state.get(TYPE).equals(SlabType.BOTTOM)) {
				return stateFrom.get(TYPE).equals(SlabType.BOTTOM) || stateFrom.get(TYPE).equals(SlabType.DOUBLE);
			} else if (state.get(TYPE).equals(SlabType.DOUBLE)) {
				return super.isSideInvisible(state, stateFrom, direction);
			} else {
				return false;
			}
		} else {
			return super.isSideInvisible(state, stateFrom, direction);
		}
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

}
