package net.ludocrypt.corners.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NylonFiberSlabBlock extends SlabBlock {

	public NylonFiberSlabBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		entity.handleFallDamage(fallDistance, 0.2F, world.getDamageSources().fall());
	}

}
