/*
 * Modified from https://gitlab.com/mikenrafter1/mc-dyn-sfx/-/releases/1.4.1
 * License included inside Jar.
 */
/*
 * Copyright (c) 2021 Andr? Schweiger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ludocrypt.dynamicsf.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.openal.EXTEfx;

import net.ludocrypt.dynamicsf.filter.ReverbFilter;
import net.ludocrypt.dynamicsf.filter.ReverbFilter.ReverbInfo;
import net.minecraft.util.Identifier;

public class ReverbSettings {

	public static final List<String> DEFAULT_IGNORED_SOUND_EVENTS = Collections.unmodifiableList(Arrays.asList("minecraft:music.creative", "minecraft:music.credits", "minecraft:music.dragon", "minecraft:music.end", "minecraft:music.game", "minecraft:music.menu", "minecraft:music.nether.basalt_deltas", "minecraft:music.nether.nether_wastes", "minecraft:music.nether.soul_sand_valley", "minecraft:music.nether.crimson_forest", "minecraft:music.nether.warped_forest", "minecraft:music.under_water", "minecraft:block.lava.pop", "minecraft:weather.rain", "minecraft:ui.button.click", "minecraft:ui.toast.challenge_complete", "minecraft:ui.toast.in", "minecraft:ui.toast.out"));

	private boolean enabled = true;

	private float reverbPercent = 1f;

	private int quality = 4;
	private boolean checkSky = true;

	private float decayFactor = 1.0F;
	private float density = EXTEfx.AL_EAXREVERB_DEFAULT_DENSITY;
	private float diffusion = EXTEfx.AL_EAXREVERB_DEFAULT_DIFFUSION;
	private float gain = EXTEfx.AL_EAXREVERB_DEFAULT_GAIN;
	private float gainHF = EXTEfx.AL_EAXREVERB_DEFAULT_GAINHF;
	private float minDecayTime = EXTEfx.AL_EAXREVERB_MIN_DECAY_TIME;
	private float decayHFRatio = EXTEfx.AL_EAXREVERB_DEFAULT_DECAY_HFRATIO;
	private float airAbsorptionGainHF = EXTEfx.AL_EAXREVERB_DEFAULT_AIR_ABSORPTION_GAINHF;
	private float reflectionsGainBase = EXTEfx.AL_EAXREVERB_DEFAULT_REFLECTIONS_GAIN;
	private float reflectionsGainMultiplier = 2.0F;
	private float reflectionsDelayMultiplier = EXTEfx.AL_EAXREVERB_DEFAULT_REFLECTIONS_DELAY;
	private float lateReverbGainBase = EXTEfx.AL_EAXREVERB_DEFAULT_LATE_REVERB_GAIN;
	private float lateReverbGainMultiplier = 2.0F;
	private float lateReverbDelayMultiplier = EXTEfx.AL_EAXREVERB_DEFAULT_LATE_REVERB_DELAY;

	public Map<Identifier, ReverbFilter.ReverbInfo> customBlockReverbMap = new HashMap<>();

	public ReverbSettings() {
	}

	public ReverbSettings(boolean enabled) {
		this.enabled = enabled;
	}

	public ReverbSettings(boolean enabled, float reverbPercent, int quality, boolean checkSky, float decayFactor, float density, float diffusion, float gain, float gainHF, float minDecayTime, float decayHFRatio, float airAbsorptionGainHF, float reflectionsGainBase, float reflectionsGainMultiplier, float reflectionsDelayMultiplier, float lateReverbGainBase, float lateReverbGainMultiplier, float lateReverbDelayMultiplier, Map<Identifier, ReverbFilter.ReverbInfo> customBlockReverbMap) {
		this.enabled = enabled;
		this.reverbPercent = reverbPercent;
		this.quality = quality;
		this.checkSky = checkSky;
		this.decayFactor = decayFactor;
		this.density = density;
		this.diffusion = diffusion;
		this.gain = gain;
		this.gainHF = gainHF;
		this.minDecayTime = minDecayTime;
		this.decayHFRatio = decayHFRatio;
		this.airAbsorptionGainHF = airAbsorptionGainHF;
		this.reflectionsGainBase = reflectionsGainBase;
		this.reflectionsGainMultiplier = reflectionsGainMultiplier;
		this.reflectionsDelayMultiplier = reflectionsDelayMultiplier;
		this.lateReverbGainBase = lateReverbGainBase;
		this.lateReverbGainMultiplier = lateReverbGainMultiplier;
		this.lateReverbDelayMultiplier = lateReverbDelayMultiplier;
		this.customBlockReverbMap = customBlockReverbMap;
	}

	public ReverbInfo getCustomBlockReverb(Identifier block) {
		return customBlockReverbMap.get(block);
	}

	public boolean getEnabled() {
		return enabled;
	}

	public float getReverbPercent() {
		return reverbPercent;
	}

	public int getQuality() {
		return quality;
	}

	public boolean getCheckSky() {
		return checkSky;
	}

	public Map<Identifier, ReverbFilter.ReverbInfo> getCustomBlockReverbMap() {
		return customBlockReverbMap;
	}

	public float getDecayFactor() {
		return decayFactor;
	}

	public float getDensity() {
		return density;
	}

	public float getDiffusion() {
		return diffusion;
	}

	public float getGain() {
		return gain;
	}

	public float getGainHF() {
		return gainHF;
	}

	public float getMinDecayTime() {
		return minDecayTime;
	}

	public float getDecayHFRatio() {
		return decayHFRatio;
	}

	public float getAirAbsorptionGainHF() {
		return airAbsorptionGainHF;
	}

	public float getReflectionsGainBase() {
		return reflectionsGainBase;
	}

	public float getReflectionsGainMultiplier() {
		return reflectionsGainMultiplier;
	}

	public float getReflectionsDelayMultiplier() {
		return reflectionsDelayMultiplier;
	}

	public float getLateReverbGainBase() {
		return lateReverbGainBase;
	}

	public float getLateReverbGainMultiplier() {
		return lateReverbGainMultiplier;
	}

	public float getLateReverbDelayMultiplier() {
		return lateReverbDelayMultiplier;
	}

	public static boolean isIgnoredSoundEvent(Identifier identifier) {
		return DEFAULT_IGNORED_SOUND_EVENTS.contains(identifier.toString()) || identifier.getPath().contains("ui.") || identifier.getPath().contains("music.");
	}

	public ReverbSettings setAirAbsorptionGainHF(float airAbsorptionGainHF) {
		this.airAbsorptionGainHF = airAbsorptionGainHF;
		return this;
	}

	public ReverbSettings setCheckSky(boolean checkSky) {
		this.checkSky = checkSky;
		return this;
	}

	public ReverbSettings setCustomBlockReverbMap(Map<Identifier, ReverbFilter.ReverbInfo> customBlockReverbMap) {
		this.customBlockReverbMap = customBlockReverbMap;
		return this;
	}

	public ReverbSettings addCustomBlockReverbMap(Identifier block, ReverbFilter.ReverbInfo reverb) {
		this.customBlockReverbMap.put(block, reverb);
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

	public ReverbSettings setLateReverbDelayMultiplier(float lateReverbDelayMultiplier) {
		this.lateReverbDelayMultiplier = lateReverbDelayMultiplier;
		return this;
	}

	public ReverbSettings setLateReverbGainBase(float lateReverbGainBase) {
		this.lateReverbGainBase = lateReverbGainBase;
		return this;
	}

	public ReverbSettings setLateReverbGainMultiplier(float lateReverbGainMultiplier) {
		this.lateReverbGainMultiplier = lateReverbGainMultiplier;
		return this;
	}

	public ReverbSettings setMinDecayTime(float minDecayTime) {
		this.minDecayTime = minDecayTime;
		return this;
	}

	public ReverbSettings setQuality(int quality) {
		this.quality = quality;
		return this;
	}

	public ReverbSettings setReflectionsDelayMultiplier(float reflectionsDelayMultiplier) {
		this.reflectionsDelayMultiplier = reflectionsDelayMultiplier;
		return this;
	}

	public ReverbSettings setReflectionsGainBase(float reflectionsGainBase) {
		this.reflectionsGainBase = reflectionsGainBase;
		return this;
	}

	public ReverbSettings setReflectionsGainMultiplier(float reflectionsGainMultiplier) {
		this.reflectionsGainMultiplier = reflectionsGainMultiplier;
		return this;
	}

	public ReverbSettings setReverbPercent(float reverbPercent) {
		this.reverbPercent = reverbPercent;
		return this;
	}

}
