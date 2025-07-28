package net.ludocrypt.corners.block;

import net.ludocrypt.limlib.api.world.Manipulation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OperatorBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public class StructureHelperBlock extends Block implements OperatorBlock {

	public static final EnumProperty<BlockRotation> ROTATION = EnumProperty.of("rotation", BlockRotation.class);

	public StructureHelperBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(ROTATION, rotation.rotate(state.get(ROTATION)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state
			.with(ROTATION, Manipulation.of(state.get(ROTATION)).manipulate(Manipulation.of(mirror)).flattenRotation());
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(ROTATION));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(ROTATION, Manipulation.of(Direction.EAST, ctx.getPlayerFacing()).getRotation());
	}

}
