package net.ludocrypt.corners.client.sound;

import org.lwjgl.openal.EXTEfx;

import net.minecraft.util.Identifier;

public class ReverbSettings {

	public boolean enabled = true;
	public float reverbPercent = 1f;
	public float decayFactor = 1f;
	public float density = EXTEfx.AL_EAXREVERB_DEFAULT_DENSITY;
	public float diffusion = EXTEfx.AL_EAXREVERB_DEFAULT_DIFFUSION;
	public float gain = EXTEfx.AL_EAXREVERB_DEFAULT_GAIN;
	public float gainHF = EXTEfx.AL_EAXREVERB_DEFAULT_GAINHF;
	public float decayTime = EXTEfx.AL_EAXREVERB_DEFAULT_DECAY_TIME;
	public float decayHFRatio = EXTEfx.AL_EAXREVERB_DEFAULT_DECAY_HFRATIO;
	public float airAbsorptionGainHF = EXTEfx.AL_EAXREVERB_DEFAULT_AIR_ABSORPTION_GAINHF;
	public float reflectionsGainBase = EXTEfx.AL_EAXREVERB_DEFAULT_REFLECTIONS_GAIN;
	public float lateReverbGainBase = EXTEfx.AL_EAXREVERB_DEFAULT_LATE_REVERB_GAIN;
	public float reflectionsDelay = EXTEfx.AL_REVERB_DEFAULT_REFLECTIONS_DELAY;
	public float lateReverbDelay = EXTEfx.AL_REVERB_DEFAULT_LATE_REVERB_DELAY;
	public int decayHFLimit = EXTEfx.AL_REVERB_DEFAULT_DECAY_HFLIMIT;

	public ReverbSettings() {
	}

	public ReverbSettings(boolean enabled) {
		this.enabled = enabled;
	}

	public static boolean shouldIgnore(Identifier identifier) {
		return identifier.getPath().contains("ui.") || identifier.getPath().contains("music.") || identifier.getPath().contains("block.lava.pop") || identifier.getPath().contains("weather.") || identifier.getPath().startsWith("atmosfera") || identifier.getPath().startsWith("dynmus");
	}

	public ReverbSettings setAirAbsorptionGainHF(float airAbsorptionGainHF) {
		this.airAbsorptionGainHF = airAbsorptionGainHF;
		return this;
	}

	public ReverbSettings setDecayFactor(float decayFactor) {
		this.decayFactor = decayFactor;
		return this;
	}

	public ReverbSettings setDecayHFRatio(float decayHFRatio) {
		this.decayHFRatio = decayHFRatio;
		return this;
	}

	public ReverbSettings setDensity(float density) {
		this.density = density;
		return this;
	}

	public ReverbSettings setDiffusion(float diffusion) {
		this.diffusion = diffusion;
		return this;
	}

	public ReverbSettings setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public ReverbSettings setGain(float gain) {
		this.gain = gain;
		return this;
	}

	public ReverbSettings setGainHF(float gainHF) {
		this.gainHF = gainHF;
		return this;
	}

	public ReverbSettings setLateReverbGainBase(float lateReverbGainBase) {
		this.lateReverbGainBase = lateReverbGainBase;
		return this;
	}

	public ReverbSettings setDecayTime(float decayTime) {
		this.decayTime = decayTime;
		return this;
	}

	public ReverbSettings setReflectionsGainBase(float reflectionsGainBase) {
		this.reflectionsGainBase = reflectionsGainBase;
		return this;
	}

	public ReverbSettings setReverbPercent(float reverbPercent) {
		this.reverbPercent = reverbPercent;
		return this;
	}

	public ReverbSettings setDecayHFLimit(int decayHFLimit) {
		this.decayHFLimit = decayHFLimit;
		return this;
	}

	public ReverbSettings setLateReverbDelay(float lateReverbDelay) {
		this.lateReverbDelay = lateReverbDelay;
		return this;
	}

	public ReverbSettings setReflectionsDelay(float reflectionsDelay) {
		this.reflectionsDelay = reflectionsDelay;
		return this;
	}

}
