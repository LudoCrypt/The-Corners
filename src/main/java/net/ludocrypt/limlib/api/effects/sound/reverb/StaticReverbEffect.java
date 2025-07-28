package net.ludocrypt.limlib.api.effects.sound.reverb;

import org.lwjgl.openal.EXTEfx;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.Identifier;

/**
 * A Reverb effect controls
 * <p>
 * This is a simplification of the base {@link ReverbEffect} class, where each
 * setting is a static, non-changing value
 */
public class StaticReverbEffect extends ReverbEffect {

	public static final Codec<StaticReverbEffect> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.BOOL.optionalFieldOf("enabled", true).stable().forGetter((reverb) -> {
			return reverb.enabled;
		}), Codec
			.floatRange(EXTEfx.AL_REVERB_MIN_DENSITY, EXTEfx.AL_REVERB_MAX_DENSITY)
			.optionalFieldOf("density", EXTEfx.AL_REVERB_DEFAULT_DENSITY)
			.stable()
			.forGetter((reverb) -> {
				return reverb.density;
			}),
			Codec
				.floatRange(EXTEfx.AL_REVERB_MIN_DIFFUSION, EXTEfx.AL_REVERB_MAX_DIFFUSION)
				.optionalFieldOf("diffusion", EXTEfx.AL_REVERB_DEFAULT_DIFFUSION)
				.stable()
				.forGetter((reverb) -> {
					return reverb.diffusion;
				}),
			Codec
				.floatRange(EXTEfx.AL_REVERB_MIN_GAIN, EXTEfx.AL_REVERB_MAX_GAIN)
				.optionalFieldOf("gain", EXTEfx.AL_REVERB_DEFAULT_GAIN)
				.stable()
				.forGetter((reverb) -> {
					return reverb.gain;
				}),
			Codec
				.floatRange(EXTEfx.AL_REVERB_MIN_GAINHF, EXTEfx.AL_REVERB_MAX_GAINHF)
				.optionalFieldOf("gain_hf", EXTEfx.AL_REVERB_DEFAULT_GAINHF)
				.stable()
				.forGetter((reverb) -> {
					return reverb.gainHF;
				}),
			Codec
				.floatRange(EXTEfx.AL_REVERB_MIN_DECAY_TIME, EXTEfx.AL_REVERB_MAX_DECAY_TIME)
				.optionalFieldOf("decay_time", EXTEfx.AL_REVERB_DEFAULT_DECAY_TIME)
				.stable()
				.forGetter((reverb) -> {
					return reverb.decayTime;
				}),
			Codec
				.floatRange(EXTEfx.AL_REVERB_MIN_DECAY_HFRATIO, EXTEfx.AL_REVERB_MAX_DECAY_HFRATIO)
				.optionalFieldOf("decay_hf_ratio", EXTEfx.AL_REVERB_DEFAULT_DECAY_HFRATIO)
				.stable()
				.forGetter((reverb) -> {
					return reverb.decayHFRatio;
				}),
			Codec
				.floatRange(EXTEfx.AL_REVERB_MIN_AIR_ABSORPTION_GAINHF, EXTEfx.AL_REVERB_MAX_AIR_ABSORPTION_GAINHF)
				.optionalFieldOf("air_absorption_gain_hf", EXTEfx.AL_REVERB_DEFAULT_AIR_ABSORPTION_GAINHF)
				.stable()
				.forGetter((reverb) -> {
					return reverb.airAbsorptionGainHF;
				}),
			Codec
				.floatRange(EXTEfx.AL_REVERB_MIN_REFLECTIONS_GAIN, EXTEfx.AL_REVERB_MAX_REFLECTIONS_GAIN)
				.optionalFieldOf("max_reflections_gain", EXTEfx.AL_REVERB_DEFAULT_REFLECTIONS_GAIN)
				.stable()
				.forGetter((reverb) -> {
					return reverb.reflectionsGainBase;
				}),
			Codec
				.floatRange(EXTEfx.AL_REVERB_MIN_LATE_REVERB_GAIN, EXTEfx.AL_REVERB_MAX_LATE_REVERB_GAIN)
				.optionalFieldOf("late_reverb_gain", EXTEfx.AL_REVERB_DEFAULT_LATE_REVERB_GAIN)
				.stable()
				.forGetter((reverb) -> {
					return reverb.lateReverbGainBase;
				}),
			Codec
				.floatRange(EXTEfx.AL_REVERB_MIN_REFLECTIONS_DELAY, EXTEfx.AL_REVERB_MAX_REFLECTIONS_DELAY)
				.optionalFieldOf("reflections_delay", EXTEfx.AL_REVERB_DEFAULT_REFLECTIONS_DELAY)
				.stable()
				.forGetter((reverb) -> {
					return reverb.reflectionsDelay;
				}),
			Codec
				.floatRange(EXTEfx.AL_REVERB_MIN_LATE_REVERB_DELAY, EXTEfx.AL_REVERB_MAX_LATE_REVERB_DELAY)
				.optionalFieldOf("late_reverb_delay", EXTEfx.AL_REVERB_DEFAULT_LATE_REVERB_DELAY)
				.stable()
				.forGetter((reverb) -> {
					return reverb.lateReverbDelay;
				}),
			Codec
				.intRange(EXTEfx.AL_REVERB_MIN_DECAY_HFLIMIT, EXTEfx.AL_REVERB_MAX_DECAY_HFLIMIT)
				.optionalFieldOf("decay_hf_limit", EXTEfx.AL_REVERB_DEFAULT_DECAY_HFLIMIT)
				.stable()
				.forGetter((reverb) -> {
					return reverb.decayHFLimit;
				}))
			.apply(instance, instance.stable(StaticReverbEffect::new));
	});

	private final boolean enabled;
	private final float density;
	private final float diffusion;
	private final float gain;
	private final float gainHF;
	private final float decayTime;
	private final float decayHFRatio;
	private final float airAbsorptionGainHF;
	private final float reflectionsGainBase;
	private final float lateReverbGainBase;
	private final float reflectionsDelay;
	private final float lateReverbDelay;
	private final int decayHFLimit;

	public StaticReverbEffect(boolean enabled, float density, float diffusion, float gain, float gainHF, float decayTime,
			float decayHFRatio, float airAbsorptionGainHF, float reflectionsGainBase, float lateReverbGainBase,
			float reflectionsDelay, float lateReverbDelay, int decayHFLimit) {
		this.enabled = enabled;
		this.density = density;
		this.diffusion = diffusion;
		this.gain = gain;
		this.gainHF = gainHF;
		this.decayTime = decayTime;
		this.decayHFRatio = decayHFRatio;
		this.airAbsorptionGainHF = airAbsorptionGainHF;
		this.reflectionsGainBase = reflectionsGainBase;
		this.lateReverbGainBase = lateReverbGainBase;
		this.reflectionsDelay = reflectionsDelay;
		this.lateReverbDelay = lateReverbDelay;
		this.decayHFLimit = decayHFLimit;
	}

	@Override
	public Codec<? extends ReverbEffect> getCodec() {
		return CODEC;
	}

	@Override
	public boolean shouldIgnore(Identifier identifier) {
		return identifier.getPath().contains("ui.") || identifier.getPath().contains("music.") || identifier
			.getPath()
			.contains("block.lava.pop") || identifier.getPath().contains("weather.") || identifier
				.getPath()
				.startsWith("atmosfera") || identifier.getPath().startsWith("dynmus");
	}

	@Override
	public boolean isEnabled(MinecraftClient client, SoundInstance soundInstance) {
		return this.enabled;
	}

	@Override
	public float getAirAbsorptionGainHF(MinecraftClient client, SoundInstance soundInstance) {
		return this.airAbsorptionGainHF;
	}

	@Override
	public float getDecayHFRatio(MinecraftClient client, SoundInstance soundInstance) {
		return this.decayHFRatio;
	}

	@Override
	public float getDensity(MinecraftClient client, SoundInstance soundInstance) {
		return this.density;
	}

	@Override
	public float getDiffusion(MinecraftClient client, SoundInstance soundInstance) {
		return this.diffusion;
	}

	@Override
	public float getGain(MinecraftClient client, SoundInstance soundInstance) {
		return this.gain;
	}

	@Override
	public float getGainHF(MinecraftClient client, SoundInstance soundInstance) {
		return this.gainHF;
	}

	@Override
	public float getLateReverbGainBase(MinecraftClient client, SoundInstance soundInstance) {
		return this.lateReverbGainBase;
	}

	@Override
	public float getDecayTime(MinecraftClient client, SoundInstance soundInstance) {
		return this.decayTime;
	}

	@Override
	public float getReflectionsGainBase(MinecraftClient client, SoundInstance soundInstance) {
		return this.reflectionsGainBase;
	}

	@Override
	public int getDecayHFLimit(MinecraftClient client, SoundInstance soundInstance) {
		return this.decayHFLimit;
	}

	@Override
	public float getLateReverbDelay(MinecraftClient client, SoundInstance soundInstance) {
		return this.lateReverbDelay;
	}

	@Override
	public float getReflectionsDelay(MinecraftClient client, SoundInstance soundInstance) {
		return this.reflectionsDelay;
	}

	public static class Builder {

		private boolean enabled = true;
		private float density = EXTEfx.AL_REVERB_DEFAULT_DENSITY;
		private float diffusion = EXTEfx.AL_REVERB_DEFAULT_DIFFUSION;
		private float gain = EXTEfx.AL_REVERB_DEFAULT_GAIN;
		private float gainHF = EXTEfx.AL_REVERB_DEFAULT_GAINHF;
		private float decayTime = EXTEfx.AL_REVERB_DEFAULT_DECAY_TIME;
		private float decayHFRatio = EXTEfx.AL_REVERB_DEFAULT_DECAY_HFRATIO;
		private float airAbsorptionGainHF = EXTEfx.AL_REVERB_DEFAULT_AIR_ABSORPTION_GAINHF;
		private float reflectionsGainBase = EXTEfx.AL_REVERB_DEFAULT_REFLECTIONS_GAIN;
		private float lateReverbGainBase = EXTEfx.AL_REVERB_DEFAULT_LATE_REVERB_GAIN;
		private float reflectionsDelay = EXTEfx.AL_REVERB_DEFAULT_REFLECTIONS_DELAY;
		private float lateReverbDelay = EXTEfx.AL_REVERB_DEFAULT_LATE_REVERB_DELAY;
		private int decayHFLimit = EXTEfx.AL_REVERB_DEFAULT_DECAY_HFLIMIT;

		public Builder setAirAbsorptionGainHF(float airAbsorptionGainHF) {
			this.airAbsorptionGainHF = airAbsorptionGainHF;
			return this;
		}

		public Builder setDecayHFRatio(float decayHFRatio) {
			this.decayHFRatio = decayHFRatio;
			return this;
		}

		public Builder setDensity(float density) {
			this.density = density;
			return this;
		}

		public Builder setDiffusion(float diffusion) {
			this.diffusion = diffusion;
			return this;
		}

		public Builder setEnabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		public Builder setGain(float gain) {
			this.gain = gain;
			return this;
		}

		public Builder setGainHF(float gainHF) {
			this.gainHF = gainHF;
			return this;
		}

		public Builder setLateReverbGainBase(float lateReverbGainBase) {
			this.lateReverbGainBase = lateReverbGainBase;
			return this;
		}

		public Builder setDecayTime(float decayTime) {
			this.decayTime = decayTime;
			return this;
		}

		public Builder setReflectionsGainBase(float reflectionsGainBase) {
			this.reflectionsGainBase = reflectionsGainBase;
			return this;
		}

		public Builder setDecayHFLimit(int decayHFLimit) {
			this.decayHFLimit = decayHFLimit;
			return this;
		}

		public Builder setLateReverbDelay(float lateReverbDelay) {
			this.lateReverbDelay = lateReverbDelay;
			return this;
		}

		public Builder setReflectionsDelay(float reflectionsDelay) {
			this.reflectionsDelay = reflectionsDelay;
			return this;
		}

		public StaticReverbEffect build() {
			return new StaticReverbEffect(this.enabled, this.density, this.diffusion, this.gain, this.gainHF, this.decayTime,
				this.decayHFRatio, this.airAbsorptionGainHF, this.reflectionsGainBase, this.lateReverbGainBase,
				this.reflectionsDelay, this.lateReverbDelay, this.decayHFLimit);
		}

	}

}
