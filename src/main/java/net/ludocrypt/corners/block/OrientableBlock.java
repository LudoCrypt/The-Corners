package net.ludocrypt.corners.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public class OrientableBlock extends Block {

	public static final EnumProperty<JigsawOrientation> ORIENTATION = Properties.ORIENTATION;

	public OrientableBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(ORIENTATION, JigsawOrientation.NORTH_UP));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(ORIENTATION);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(ORIENTATION, rotation.getDirectionTransformation().mapJigsawOrientation(state.get(ORIENTATION)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.with(ORIENTATION, mirror.getDirectionTransformation().mapJigsawOrientation(state.get(ORIENTATION)));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction;
		Direction direction2;

		if (ctx.getSide().getAxis() == Direction.Axis.Y) {
			direction = ctx.getPlayerFacing().getOpposite();
			direction2 = Direction.UP;
		} else {
			direction = Direction.UP;
			direction2 = ctx.getPlayerFacing().getOpposite();
		}

		JigsawOrientation ore = JigsawOrientation.byDirections(direction, direction2);
		return this.getDefaultState().with(ORIENTATION, ore);
	}

	public static Direction getFacing(BlockState state) {
		return ((JigsawOrientation) state.get(ORIENTATION)).getFacing();
	}

	public static Direction getRotation(BlockState state) {
		return ((JigsawOrientation) state.get(ORIENTATION)).getRotation();
	}

}
