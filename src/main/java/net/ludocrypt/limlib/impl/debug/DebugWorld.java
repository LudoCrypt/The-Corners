package net.ludocrypt.limlib.impl.debug;

import java.util.Map;

import com.mojang.serialization.Lifecycle;

import net.ludocrypt.limlib.api.LimlibRegistrar;
import net.ludocrypt.limlib.api.LimlibRegistryHooks;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionTypes;

public class DebugWorld implements LimlibRegistrar {

	public static final RegistryKey<GeneratorType> DEBUG_KEY = RegistryKey
		.of(RegistryKeys.GENERATOR_TYPE, new Identifier("limlib", "debug_nbt"));

	@Override
	public void registerHooks() {
		LimlibRegistryHooks
			.hook(RegistryKeys.GENERATOR_TYPE, (infoLookup, registryKey, registry) -> registry
				.register(DEBUG_KEY, new GeneratorType(Map
					.of(DimensionOptions.OVERWORLD,
						new DimensionOptions(
							infoLookup
								.lookup(RegistryKeys.DIMENSION_TYPE)
								.get()
								.getter()
								.getHolderOrThrow(DimensionTypes.OVERWORLD),
							new DebugNbtChunkGenerator(
								infoLookup.lookup(RegistryKeys.BIOME).get().getter().getHolderOrThrow(Biomes.THE_VOID))))),
					Lifecycle.stable()));
	}

}
