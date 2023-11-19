package net.ludocrypt.corners.world.feature;

import com.mojang.serialization.Codec;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.block.RadioBlock;
import net.ludocrypt.corners.init.CornerBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class GaiaTreeFeature extends Feature<DefaultFeatureConfig> {

	public GaiaTreeFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeatureContext<DefaultFeatureConfig> context) {
		RandomGenerator random = context.getRandom();
		StructureWorldAccess world = context.getWorld();
		BlockPos pos = context.getOrigin().toImmutable();
		BlockState stump = CornerBlocks.STRIPPED_GAIA_LOG.getDefaultState();
		BlockState leaf = CornerBlocks.GAIA_LEAVES.getDefaultState().with(LeavesBlock.DISTANCE, 1);
		trySetState(world, pos.up(), stump);
		trySetState(world, pos.down(), stump);
		trySetState(world, pos.up().up(), leaf);
		trySetState(world, pos.up().north(), leaf);
		trySetState(world, pos.up().east(), leaf);
		trySetState(world, pos.up().south(), leaf);
		trySetState(world, pos.up().west(), leaf);
		trySetState(world, pos.down().north(), stump);
		trySetState(world, pos.down().east(), stump);
		trySetState(world, pos.down().south(), stump);
		trySetState(world, pos.down().west(), stump);
		int range = random.nextInt(3) + 4;

		for (int i = -range; i <= range; i++) {

			for (int j = -range; j <= range; j++) {

				for (int k = -range; k <= range; k++) {
					BlockPos op = pos.add(i, j, k);

					if (!op.equals(pos.offset(world.getBlockState(pos).get(RadioBlock.FACING)))) {

						if (world.getBlockState(op).isOf(Blocks.VINE) || world.isAir(op)) {

							for (Direction dir : Direction.values()) {

								if (!dir.equals(Direction.DOWN)) {

									if (world.getBlockState(op.offset(dir)).isSideSolidFullSquare(world, op.offset(dir), dir.getOpposite())) {

										if (random.nextDouble() > op.getSquaredDistance(pos) / (double) range / 2.0) {
											BlockState defaultState = Blocks.VINE.getDefaultState();

											if (world.getBlockState(op).isOf(Blocks.VINE)) {
												defaultState = world.getBlockState(op);
											}

											world.setBlockState(op, defaultState.with(VineBlock.getFacingProperty(dir), true), Block.NOTIFY_ALL);
										}

									}

								}

							}

						}

					}

				}

			}

		}

		return true;
	}

	public void trySetState(StructureWorldAccess world, BlockPos pos, BlockState state) {
		BlockState from = world.getBlockState(pos);
		boolean wooden = from.isIn(TagKey.of(RegistryKeys.BLOCK, TheCorners.id("gaia_replaceable")));

		if (wooden || !from.isFullCube(world, pos) || from.isAir()) {

			if (!from.isFullCube(world, pos) && !(wooden) && !from.isOf(Blocks.VINE) && !from.isAir()) {
				world.breakBlock(pos, true);
			}

			world.setBlockState(pos, state, Block.NOTIFY_ALL);
		}

	}

}
