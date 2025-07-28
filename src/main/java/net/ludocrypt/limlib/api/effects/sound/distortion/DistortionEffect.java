package net.ludocrypt.limlib.api.effects.sound.distortion;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import net.ludocrypt.limlib.impl.mixin.RegistriesAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

/**
 * A Distortion effect controls
 */
public abstract class DistortionEffect {

	public static final RegistryKey<Registry<Codec<? extends DistortionEffect>>> DISTORTION_EFFECT_CODEC_KEY = RegistryKey
		.ofRegistry(new Identifier("limlib/codec/distortion_effect"));
	public static final Registry<Codec<? extends DistortionEffect>> DISTORTION_EFFECT_CODEC = RegistriesAccessor
		.callRegisterSimple(DISTORTION_EFFECT_CODEC_KEY, Lifecycle.stable(), (registry) -> StaticDistortionEffect.CODEC);
	public static final Codec<DistortionEffect> CODEC = DISTORTION_EFFECT_CODEC
		.getCodec()
		.dispatchStable(DistortionEffect::getCodec, Function.identity());

	public abstract Codec<? extends DistortionEffect> getCodec();

	public static void init() {
		Registry
			.register(DistortionEffect.DISTORTION_EFFECT_CODEC, new Identifier("limlib", "static"),
				StaticDistortionEffect.CODEC);
	}

	/**
	 * Whether or not a Sound Event should be ignored
	 * 
	 * @param identifier the Identifier of the Sound Event
	 */
	public abstract boolean shouldIgnore(Identifier identifier);

	public abstract boolean isEnabled(MinecraftClient client, SoundInstance soundInstance);

	public abstract float getEdge(MinecraftClient client, SoundInstance soundInstance);

	public abstract float getGain(MinecraftClient client, SoundInstance soundInstance);

	public abstract float getLowpassCutoff(MinecraftClient client, SoundInstance soundInstance);

	public abstract float getEQCenter(MinecraftClient client, SoundInstance soundInstance);

	public abstract float getEQBandWidth(MinecraftClient client, SoundInstance soundInstance);

}
