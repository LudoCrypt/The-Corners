package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import java.util.OptionalLong;

import ladysnake.satin.api.managed.ManagedShaderEffect;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.util.RegistryHelper;
import net.ludocrypt.corners.world.chunk.SingleBlockChunkGenerator;
import net.ludocrypt.corners.world.chunk.YearningCanalChunkGenerator;
import net.ludocrypt.dynamicsf.config.ReverbSettings;
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
	public static final DimensionType YEARNING_CANAL_DIMENSION_TYPE = DimensionType.create(OptionalLong.of(1200), true, false, false, false, 1.0, false, false, true, false, false, 0, 2032, 2032, DirectBiomeAccessType.INSTANCE, YEARNING_CANAL_ID, YEARNING_CANAL_ID, 1.0F);
	public static final RegistryKey<DimensionType> YEARNING_CANAL_DIMENSION_TYPE_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, YEARNING_CANAL_ID);
	public static final RegistryKey<DimensionOptions> YEARNING_CANAL_DIMENSION_OPTOINS_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_KEY, YEARNING_CANAL_ID);
	public static final RegistryKey<World> YEARNING_CANAL_WORLD_REGISTRY_KEY = RegistryKey.of(Registry.WORLD_KEY, YEARNING_CANAL_ID);
	public static final ReverbSettings YEARNING_CANAL_REVERB = get(YEARNING_CANAL, new ReverbSettings());
	public static final ManagedShaderEffect YEARNING_CANAL_SHADER = get(YEARNING_CANAL, YEARNING_CANAL);

	public static void init() {
		RegistryHelper.get("single_block_chunk_generator", SingleBlockChunkGenerator.CODEC);
		RegistryHelper.get("yearning_canal_chunk_generator", YearningCanalChunkGenerator.CODEC);
	}

}