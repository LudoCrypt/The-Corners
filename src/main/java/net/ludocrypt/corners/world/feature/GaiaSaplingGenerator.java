package net.ludocrypt.corners.world.feature;

import net.ludocrypt.corners.init.CornerBiomes;
import net.minecraft.block.BlockState;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.registry.Holder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class GaiaSaplingGenerator extends SaplingGenerator {

	@Override
	protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(RandomGenerator random, boolean bees) {
		return CornerBiomes.CONFIGURED_SAPLING_GAIA_TREE_FEATURE;
	}

	public boolean generateRadio(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state,
			RandomGenerator random) {
		Holder<ConfiguredFeature<?, ?>> holder = world
			.getRegistryManager()
			.get(RegistryKeys.CONFIGURED_FEATURE)
			.getHolder(CornerBiomes.CONFIGURED_GAIA_TREE_FEATURE)
			.orElse(null);

		if (holder == null) {
			return false;
		} else {
			return holder.value().generate(world, chunkGenerator, random, pos);
		}

	}

}
