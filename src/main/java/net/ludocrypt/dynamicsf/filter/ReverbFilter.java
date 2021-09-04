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
package net.ludocrypt.dynamicsf.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.openal.EXTEfx;

import com.mojang.datafixers.util.Pair;

import net.ludocrypt.dynamicsf.config.ReverbSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;

public class ReverbFilter {
	private static int id = -1;
	private static int slot = -1;

	private static boolean enabled = false;
	private static boolean checkSky = true;
	private static int ticks = 0;
	private static float prevDecayFactor = 0f;
	private static float prevRoomFactor = 0f;
	private static float prevSkyFactor = 0f;

	private static float density = 0.2f;
	private static float diffusion = 0.6f;
	private static float gain = 0.15f;
	private static float gainHF = 0.8f;
	private static float decayTime = 0.1f;
	private static float decayHFRatio = 0.7f;
	private static float reflectionsGain = 0f;
	private static float reflectionsDelay = 0f;
	private static float lateReverbGain = 0f;
	private static float lateReverbDelay = 0f;
	private static float airAbsorptionGainHF = 0.99f;
	private static float roomRolloffFactor = 0f;
	private static int decayHFLimit = 1;

	private static int[] scanSizes = new int[] { 30, 100, 30, 10, 30 };
	private static final Direction[] validationOffsets = new Direction[] { Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };
	private static final Vec3d initPoint = new Vec3d(0d, 0d, 1d).rotateX(-22.5f);

	private static Vec3d tracer = initPoint;
	private static float sky = 0;

	private static int quality = 4;
	private static Vec3d halfBox = new Vec3d(quality, quality, quality).multiply(0.5);
	private static List<Pair<BlockPos, List<Pair<Identifier, Material>>>> surfaces = new ArrayList<>();

	private static List<Material> HIGH_REVERB_MATERIALS = Arrays.asList(Material.STONE, Material.GLASS, Material.ICE, Material.DENSE_ICE, Material.METAL);
	private static List<Material> LOW_REVERB_MATERIALS = Arrays.asList(Material.WOOL, Material.CARPET, Material.LEAVES, Material.PLANT, Material.UNDERWATER_PLANT, Material.REPLACEABLE_PLANT, Material.REPLACEABLE_UNDERWATER_PLANT, Material.SOLID_ORGANIC, Material.GOURD, Material.CACTUS, Material.COBWEB, Material.CAKE, Material.SPONGE, Material.SNOW_LAYER, Material.SNOW_BLOCK, Material.WOOD);

	public static void reinit() {
		id = EXTEfx.alGenEffects();
		slot = EXTEfx.alGenAuxiliaryEffectSlots();
	}

	public static void updateGlobal(final boolean verdict, final MinecraftClient client, final ReverbSettings data, final Vec3d clientPos) {
		if (verdict) {
			update(client, data, clientPos);
		} else {
			reset(data);
		}
	}

	public static boolean updateSoundInstance(final SoundInstance soundInstance) {
		if (id == -1 || slot == -1) {
			reinit();
		}

		if (!enabled || reflectionsDelay + lateReverbDelay <= 0)
			return false;

		if (soundInstance.getAttenuationType() == SoundInstance.AttenuationType.LINEAR) {
			roomRolloffFactor = 2f / (Math.max(soundInstance.getVolume(), 1f) + 2f);
		} else {
			roomRolloffFactor = 0f;
		}

		EXTEfx.alAuxiliaryEffectSlotf(slot, EXTEfx.AL_EFFECTSLOT_GAIN, 0);
		EXTEfx.alEffecti(id, EXTEfx.AL_EFFECT_TYPE, EXTEfx.AL_EFFECT_REVERB);
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_DENSITY, MathHelper.clamp(density, EXTEfx.AL_REVERB_MIN_DENSITY, EXTEfx.AL_REVERB_MAX_DENSITY));
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_DIFFUSION, MathHelper.clamp(diffusion, EXTEfx.AL_REVERB_MIN_DIFFUSION, EXTEfx.AL_REVERB_MAX_DIFFUSION));
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_GAIN, MathHelper.clamp(gain, EXTEfx.AL_REVERB_MIN_GAIN, EXTEfx.AL_REVERB_MAX_GAIN));
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_GAINHF, MathHelper.clamp(gainHF, EXTEfx.AL_REVERB_MIN_GAINHF, EXTEfx.AL_REVERB_MAX_GAINHF));
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_DECAY_TIME, MathHelper.clamp(decayTime, EXTEfx.AL_REVERB_MIN_DECAY_TIME, EXTEfx.AL_REVERB_MAX_DECAY_TIME));
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_DECAY_HFRATIO, MathHelper.clamp(decayHFRatio, EXTEfx.AL_REVERB_MIN_DECAY_HFRATIO, EXTEfx.AL_REVERB_MAX_DECAY_HFRATIO));
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_REFLECTIONS_GAIN, MathHelper.clamp(reflectionsGain, EXTEfx.AL_REVERB_MIN_REFLECTIONS_GAIN, EXTEfx.AL_REVERB_MAX_REFLECTIONS_GAIN));
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_REFLECTIONS_DELAY, MathHelper.clamp(reflectionsDelay, EXTEfx.AL_REVERB_MIN_REFLECTIONS_DELAY, EXTEfx.AL_REVERB_MAX_REFLECTIONS_DELAY));
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_LATE_REVERB_GAIN, MathHelper.clamp(lateReverbGain, EXTEfx.AL_REVERB_MIN_LATE_REVERB_GAIN, EXTEfx.AL_REVERB_MAX_LATE_REVERB_GAIN));
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_LATE_REVERB_DELAY, MathHelper.clamp(lateReverbDelay, EXTEfx.AL_REVERB_MIN_LATE_REVERB_DELAY, EXTEfx.AL_REVERB_MAX_LATE_REVERB_DELAY));
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_AIR_ABSORPTION_GAINHF, MathHelper.clamp(airAbsorptionGainHF, EXTEfx.AL_REVERB_MIN_AIR_ABSORPTION_GAINHF, EXTEfx.AL_REVERB_MAX_AIR_ABSORPTION_GAINHF));
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_ROOM_ROLLOFF_FACTOR, MathHelper.clamp(roomRolloffFactor, EXTEfx.AL_REVERB_MIN_ROOM_ROLLOFF_FACTOR, EXTEfx.AL_REVERB_MAX_ROOM_ROLLOFF_FACTOR));
		EXTEfx.alEffecti(id, EXTEfx.AL_REVERB_DECAY_HFLIMIT, MathHelper.clamp(decayHFLimit, EXTEfx.AL_REVERB_MIN_DECAY_HFLIMIT, EXTEfx.AL_REVERB_MAX_DECAY_HFLIMIT));
		EXTEfx.alAuxiliaryEffectSloti(slot, EXTEfx.AL_EFFECTSLOT_EFFECT, id);
		EXTEfx.alAuxiliaryEffectSlotf(slot, EXTEfx.AL_EFFECTSLOT_GAIN, 1);

		return true;
	}

	public static int getSlot() {
		return slot;
	}

	private static void reset(final ReverbSettings data) {
		enabled = false;
		density = data.getDensity();
		diffusion = data.getDiffusion();
		gain = data.getGain();
		gainHF = data.getGainHF();
		decayTime = data.getMinDecayTime();
		decayHFRatio = data.getDecayHFRatio();
		reflectionsGain = 0;
		reflectionsDelay = 0;
		lateReverbGain = 0;
		lateReverbDelay = 0;
		airAbsorptionGainHF = data.getAirAbsorptionGainHF();
		roomRolloffFactor = 0.0f;

		tracer = initPoint;
		sky = 0;

		quality = data.getQuality();
		halfBox = new Vec3d(quality, quality, quality).multiply(0.5);
		scanSizes = new int[] { quality * 8, quality * 20, quality * 8, quality * 8, quality * 20 };
	}

	private static void update(final MinecraftClient client, final ReverbSettings data, final Vec3d clientPos) {
		enabled = data.getEnabled();
		if (!enabled) {
			return;
		}

		if (ticks < 16) {
			final Object[] raycast = trace(client, clientPos, scanSizes[(int) ticks / 4], tracer);
			final boolean foundSurface = (boolean) raycast[0];

			if (foundSurface) {
				Vec3d pos = (Vec3d) raycast[1];
				final BlockPos blockPos = new BlockPos(pos);
				int surface = 0;

				for (Direction direction : validationOffsets) {
					BlockPos bPos = blockPos.offset(direction, 1);
					if (client.world.getBlockState(bPos).isFullCube(client.world, bPos))
						surface++;
				}

				if (surface >= 3) {
					List<Pair<Identifier, Material>> materials = new ArrayList<>();
					pos = pos.subtract(halfBox);

					for (double x = 0; x < quality; x++) {
						for (double z = 0; z < quality; z++) {
							for (double y = 0; y < quality; y++) {
								final BlockState blockState = client.world.getBlockState(new BlockPos(pos.add(x, y, z)));
								final Material material = blockState.getMaterial();
								final Identifier blockID = Registry.BLOCK.getId(blockState.getBlock());
								if (material.blocksMovement() || !(material == Material.AIR || material == Material.WATER || material == Material.LAVA))
									materials.add(new Pair<>(blockID, material));
							}
						}
					}
					surfaces.add(new Pair<>(blockPos, materials));
				}
			}

			tracer = tracer.rotateY(90);
			if (++ticks % 4 == 0) {
				tracer = tracer.rotateX(22.5f);
			}
		} else if (ticks < 20) {
			if (ticks == 18) {
				final int scanSize = scanSizes[3];
				final Object[] raycast = trace(client, clientPos, scanSize, new Vec3d(0, -1, 0));
				if ((boolean) raycast[0] && Math.abs((int) raycast[2] - scanSize) > scanSize / 3) {
					sky += (int) raycast[2] / 3;
				}
			}
			ticks++;
		} else {
			ticks = 0;
			tracer = initPoint;

			checkSky = data.getCheckSky();
			halfBox = new Vec3d(quality, quality, quality).multiply(0.5);

			final float reverbPercent = data.getReverbPercent();
			final float minDecayTime = data.getMinDecayTime();
			final float reflectionGainBase = data.getReflectionsGainBase();
			final float reflectionGainMultiplier = data.getReflectionsGainMultiplier();
			final float reflectionDelayMultiplier = data.getReflectionsDelayMultiplier();
			final float lateReverbGainBase = data.getLateReverbGainBase();
			final float lateReverbGainMultiplier = data.getLateReverbGainMultiplier();
			final float lateReverbDelayMultiplier = data.getLateReverbDelayMultiplier();

			float decayFactor = data.getDecayFactor();
			double highReverb = 0d;
			double midReverb = 0d;
			double lowReverb = 0d;

			for (Pair<BlockPos, List<Pair<Identifier, Material>>> surface : surfaces) {
				final List<Pair<Identifier, Material>> stats = surface.getSecond();
				for (Pair<Identifier, Material> block : stats) {
					final ReverbInfo customReverb = data.getCustomBlockReverb(block.getFirst());
					if (customReverb == null) {
						if (HIGH_REVERB_MATERIALS.contains(block.getSecond())) {
							highReverb++;
						} else if (LOW_REVERB_MATERIALS.contains(block.getSecond())) {
							lowReverb++;
						} else {
							midReverb++;
						}
					} else {
						switch (customReverb) {
						case HIGH:
							highReverb++;
							break;
						case LOW:
							lowReverb++;
							break;
						default:
							midReverb++;
							break;
						}
					}
				}
			}

			surfaces = new ArrayList<>();

			if (highReverb + midReverb + lowReverb > 0d) {
				decayFactor += (highReverb - lowReverb) / (highReverb + midReverb + lowReverb);
			}

			final int surfaceCount = Math.max(1, surfaces.size());
			float reverbage = surfaceCount / 8f;

			sky = Math.min(30, Math.max(0.1f, sky / surfaceCount * 6f));

			decayFactor = (decayFactor + prevDecayFactor) / 2f;
			prevDecayFactor = decayFactor;

			reverbage = (reverbage + prevRoomFactor) / 2f;
			prevRoomFactor = reverbage;

			sky = (sky + prevSkyFactor) / 2f;
			prevSkyFactor = sky;

			decayTime = Math.max(minDecayTime, (reverbPercent + sky) * 6f * decayFactor * reverbage);
			reflectionsGain = (reverbPercent + sky) * (reflectionGainBase + reflectionGainMultiplier * reverbage);
			reflectionsDelay = reflectionDelayMultiplier * reverbage;
			lateReverbGain = reverbPercent * (lateReverbGainBase + lateReverbGainMultiplier * reverbage);
			lateReverbDelay = lateReverbDelayMultiplier * reverbage;

			sky = 0;
		}
	}

	private static Object[] trace(final MinecraftClient client, Vec3d pos, int range, final Vec3d tracer) {
		BlockPos blockPos = new BlockPos(pos);
		boolean foundSurface = false;
		int steps = 0;
		range = Math.max(1, range);
		for (; steps < range; steps++) {
			blockPos = new BlockPos(pos);
			if (client.world.getBlockState(blockPos).isFullCube(client.world, blockPos)) {
				if (steps > 3) {
					foundSurface = true;
				}
				break;
			} else if (client.world.getFluidState(blockPos).isStill()) {
				break;
			}
			pos = pos.add(tracer);
			if (checkSky && steps % 5 == 1 && hasSkyAbove(client.world, blockPos))
				sky++;
		}
		return new Object[] { foundSurface, pos, steps };
	}

	private static boolean hasSkyAbove(final ClientWorld world, final BlockPos pos) {
		if (world.getDimension().hasCeiling()) {
			return false;
		}

		final Chunk chunk = world.getChunk(pos);
		final Heightmap heightMap = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING);
		final int x = Math.abs(pos.getX() % 16);
		final int z = Math.abs(pos.getZ() % 16);
		return heightMap != null && heightMap.get(x, z) <= pos.getY();
	}

	public static enum ReverbInfo {
		HIGH, MID, LOW;

		public static ReverbInfo fromName(String name) {
			name = name.toUpperCase();
			return ReverbInfo.valueOf(name);
		}
	}
}
