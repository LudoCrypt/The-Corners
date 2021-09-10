package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.getRadio;

import java.util.OptionalLong;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.config.CornerConfig;
import net.ludocrypt.corners.util.RegistryHelper;
import net.ludocrypt.corners.world.chunk.SingleBlockChunkGenerator;
import net.ludocrypt.corners.world.chunk.YearningCanalChunkGenerator;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.DirectBiomeAccessType;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

public class CornerWorld {

	public static final String YEARNING_CANAL = "yearning_canal";
	public static final Identifier YEARNING_CANAL_ID = TheCorners.id(YEARNING_CANAL);
	public static final DimensionType YEARNING_CANAL_DIMENSION_TYPE = DimensionType.create(OptionalLong.of(1200), true, false, false, false, 1.0, false, false, true, false, false, 0, CornerConfig.getInstance().bigDimensions ? 2032 : 432, CornerConfig.getInstance().bigDimensions ? 2032 : 432, DirectBiomeAccessType.INSTANCE, YEARNING_CANAL_ID, YEARNING_CANAL_ID, 1.0F);
	public static final RegistryKey<DimensionType> YEARNING_CANAL_DIMENSION_TYPE_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, YEARNING_CANAL_ID);
	public static final RegistryKey<DimensionOptions> YEARNING_CANAL_DIMENSION_OPTOINS_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_KEY, YEARNING_CANAL_ID);
	public static final RegistryKey<World> YEARNING_CANAL_WORLD_REGISTRY_KEY = RegistryKey.of(Registry.WORLD_KEY, YEARNING_CANAL_ID);
	public static final SoundEvent YEARNING_CANAL_RADIO = getRadio(YEARNING_CANAL, CornerSoundEvents.RADIO_YEARNING_CANAL);

	public static void init() {
		RegistryHelper.get("single_block_chunk_generator", SingleBlockChunkGenerator.CODEC);
		RegistryHelper.get("yearning_canal_chunk_generator", YearningCanalChunkGenerator.CODEC);
	}

}
