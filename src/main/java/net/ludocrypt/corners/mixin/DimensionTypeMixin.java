package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.serialization.Lifecycle;

import net.ludocrypt.corners.init.CornerWorld;
import net.ludocrypt.corners.world.chunk.CommunalCorridorsChunkGenerator;
import net.ludocrypt.corners.world.chunk.YearningCanalChunkGenerator;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

@Mixin(DimensionType.class)
public class DimensionTypeMixin {

	@Inject(method = "addRegistryDefaults", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private static void corners$addRegistryDefaults(DynamicRegistryManager.Impl registryManager, CallbackInfoReturnable<DynamicRegistryManager.Impl> ci, MutableRegistry<DimensionType> mutableRegistry) {
		mutableRegistry.add(CornerWorld.YEARNING_CANAL_DIMENSION_TYPE_REGISTRY_KEY, CornerWorld.YEARNING_CANAL_DIMENSION_TYPE, Lifecycle.stable());
		mutableRegistry.add(CornerWorld.COMMUNAL_CORRIDORS_DIMENSION_TYPE_REGISTRY_KEY, CornerWorld.COMMUNAL_CORRIDORS_DIMENSION_TYPE, Lifecycle.stable());
	}

	@Inject(method = "createDefaultDimensionOptions", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private static void corners$createDefaultDimensionOptions(Registry<DimensionType> dimensionRegistry, Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry, long seed, CallbackInfoReturnable<SimpleRegistry<DimensionOptions>> ci, SimpleRegistry<DimensionOptions> simpleRegistry) {
		simpleRegistry.add(CornerWorld.YEARNING_CANAL_DIMENSION_OPTOINS_REGISTRY_KEY, new DimensionOptions(() -> dimensionRegistry.getOrThrow(CornerWorld.YEARNING_CANAL_DIMENSION_TYPE_REGISTRY_KEY), new YearningCanalChunkGenerator(new FixedBiomeSource(biomeRegistry.getOrThrow(CornerWorld.YEARNING_CANAL_BIOME)), seed)), Lifecycle.stable());
		simpleRegistry.add(CornerWorld.COMMUNAL_CORRIDORS_DIMENSION_OPTOINS_REGISTRY_KEY, new DimensionOptions(() -> dimensionRegistry.getOrThrow(CornerWorld.COMMUNAL_CORRIDORS_DIMENSION_TYPE_REGISTRY_KEY), new CommunalCorridorsChunkGenerator(new FixedBiomeSource(biomeRegistry.getOrThrow(CornerWorld.COMMUNAL_CORRIDORS_BIOME)), seed)), Lifecycle.stable());
	}

}
