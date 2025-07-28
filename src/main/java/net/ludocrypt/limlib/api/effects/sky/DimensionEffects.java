package net.ludocrypt.limlib.api.effects.sky;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.quiltmc.loader.api.ModInternal;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import net.ludocrypt.limlib.impl.mixin.RegistriesAccessor;
import net.minecraft.client.render.DimensionVisualEffects;
import net.minecraft.registry.HolderLookup;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

/**
 * A non-client-side clone of {@link DimensionVisualEffects}
 */
public abstract class DimensionEffects {

	public static final RegistryKey<Registry<Codec<? extends DimensionEffects>>> DIMENSION_EFFECTS_CODEC_KEY = RegistryKey
		.ofRegistry(new Identifier("limlib/codec/dimension_effects"));
	public static final Registry<Codec<? extends DimensionEffects>> DIMENSION_EFFECTS_CODEC = RegistriesAccessor
		.callRegisterSimple(DIMENSION_EFFECTS_CODEC_KEY, Lifecycle.stable(), (registry) -> StaticDimensionEffects.CODEC);
	public static final Codec<DimensionEffects> CODEC = DIMENSION_EFFECTS_CODEC
		.getCodec()
		.dispatchStable(DimensionEffects::getCodec, Function.identity());
	public static final RegistryKey<Registry<DimensionEffects>> DIMENSION_EFFECTS_KEY = RegistryKey
		.ofRegistry(new Identifier("limlib/dimension_effects"));

	@ModInternal
	public static final AtomicReference<HolderLookup<DimensionEffects>> MIXIN_WORLD_LOOKUP = new AtomicReference<HolderLookup<DimensionEffects>>();

	public abstract Codec<? extends DimensionEffects> getCodec();

	public static void init() {
		Registry
			.register(DimensionEffects.DIMENSION_EFFECTS_CODEC, new Identifier("limlib", "static"),
				StaticDimensionEffects.CODEC);
		Registry
			.register(DimensionEffects.DIMENSION_EFFECTS_CODEC, new Identifier("limlib", "empty"),
				EmptyDimensionEffects.CODEC);
	}

	@ClientOnly
	public abstract DimensionVisualEffects getDimensionEffects();

	public abstract float getSkyShading();

}
