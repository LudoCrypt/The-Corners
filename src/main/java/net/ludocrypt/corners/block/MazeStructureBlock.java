package net.ludocrypt.corners.block;

import net.ludocrypt.corners.block.entity.MazeStructureBlockBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.util.math.BlockPos;

public class MazeStructureBlock extends StructureBlock {

	public MazeStructureBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(MODE, StructureBlockMode.SAVE));
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new MazeStructureBlockBlockEntity(pos, state);
	}

}
