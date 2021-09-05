package net.ludocrypt.corners.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.state.StateManager.Builder;

public class DebugPaintingSpawnerBlock extends HorizontalFacingBlock {

	public final PaintingMotive motive;

	public DebugPaintingSpawnerBlock(PaintingMotive motive, Settings settings) {
		super(settings);
		this.motive = motive;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(FACING);
	}

}
