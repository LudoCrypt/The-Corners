package net.ludocrypt.limlib.api.effects.sound.reverb;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.EXTEfx;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import net.ludocrypt.limlib.api.effects.LookupGrabber;
import net.ludocrypt.limlib.api.effects.sound.SoundEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.MathHelper;

@ClientOnly
public class ReverbFilter {

	public static final Logger LOGGER = LogManager.getLogger("LimLib | Reverb");

	public static int id = -1;
	public static int slot = -1;

	public static void update() {
		id = EXTEfx.alGenEffects();
		slot = EXTEfx.alGenAuxiliaryEffectSlots();
	}

	public static boolean update(SoundInstance soundInstance, ReverbEffect data) {

		if (id == -1 || slot == -1) {
			update();
		}

		MinecraftClient client = MinecraftClient.getInstance();

		if (data.isEnabled(client, soundInstance)) {
			EXTEfx.alAuxiliaryEffectSlotf(slot, EXTEfx.AL_EFFECTSLOT_GAIN, 0);
			EXTEfx.alEffecti(id, EXTEfx.AL_EFFECT_TYPE, EXTEfx.AL_EFFECT_REVERB);
			EXTEfx
				.alEffectf(id, EXTEfx.AL_REVERB_DENSITY,
					MathHelper
						.clamp(data.getDensity(client, soundInstance), EXTEfx.AL_REVERB_MIN_DENSITY,
							EXTEfx.AL_REVERB_MAX_DENSITY));
			EXTEfx
				.alEffectf(id, EXTEfx.AL_REVERB_DIFFUSION,
					MathHelper
						.clamp(data.getDiffusion(client, soundInstance), EXTEfx.AL_REVERB_MIN_DIFFUSION,
							EXTEfx.AL_REVERB_MAX_DIFFUSION));
			EXTEfx
				.alEffectf(id, EXTEfx.AL_REVERB_GAIN, MathHelper
					.clamp(data.getGain(client, soundInstance), EXTEfx.AL_REVERB_MIN_GAIN, EXTEfx.AL_REVERB_MAX_GAIN));
			EXTEfx
				.alEffectf(id, EXTEfx.AL_REVERB_GAINHF, MathHelper
					.clamp(data.getGainHF(client, soundInstance), EXTEfx.AL_REVERB_MIN_GAINHF, EXTEfx.AL_REVERB_MAX_GAINHF));
			EXTEfx
				.alEffectf(id, EXTEfx.AL_REVERB_DECAY_TIME,
					MathHelper
						.clamp(data.getDecayTime(client, soundInstance), EXTEfx.AL_REVERB_MIN_DECAY_TIME,
							EXTEfx.AL_REVERB_MAX_DECAY_TIME));
			EXTEfx
				.alEffectf(id, EXTEfx.AL_REVERB_DECAY_HFRATIO,
					MathHelper
						.clamp(data.getDecayHFRatio(client, soundInstance), EXTEfx.AL_REVERB_MIN_DECAY_HFRATIO,
							EXTEfx.AL_REVERB_MAX_DECAY_HFRATIO));
			EXTEfx
				.alEffectf(id, EXTEfx.AL_REVERB_REFLECTIONS_GAIN,
					MathHelper
						.clamp(data.getReflectionsGainBase(client, soundInstance), EXTEfx.AL_REVERB_MIN_REFLECTIONS_GAIN,
							EXTEfx.AL_REVERB_MAX_REFLECTIONS_GAIN));
			EXTEfx
				.alEffectf(id, EXTEfx.AL_REVERB_REFLECTIONS_DELAY,
					MathHelper
						.clamp(data.getReflectionsDelay(client, soundInstance), EXTEfx.AL_REVERB_MIN_REFLECTIONS_DELAY,
							EXTEfx.AL_REVERB_MAX_REFLECTIONS_DELAY));
			EXTEfx
				.alEffectf(id, EXTEfx.AL_REVERB_LATE_REVERB_GAIN,
					MathHelper
						.clamp(data.getLateReverbGainBase(client, soundInstance), EXTEfx.AL_REVERB_MIN_LATE_REVERB_GAIN,
							EXTEfx.AL_REVERB_MAX_LATE_REVERB_GAIN));
			EXTEfx
				.alEffectf(id, EXTEfx.AL_REVERB_LATE_REVERB_DELAY,
					MathHelper
						.clamp(data.getLateReverbDelay(client, soundInstance), EXTEfx.AL_REVERB_MIN_LATE_REVERB_DELAY,
							EXTEfx.AL_REVERB_MAX_LATE_REVERB_DELAY));
			EXTEfx
				.alEffectf(id, EXTEfx.AL_REVERB_AIR_ABSORPTION_GAINHF,
					MathHelper
						.clamp(data.getAirAbsorptionGainHF(client, soundInstance),
							EXTEfx.AL_REVERB_MIN_AIR_ABSORPTION_GAINHF, EXTEfx.AL_REVERB_MAX_AIR_ABSORPTION_GAINHF));
			EXTEfx
				.alEffectf(id, EXTEfx.AL_REVERB_ROOM_ROLLOFF_FACTOR,
					MathHelper
						.clamp(
							soundInstance.getAttenuationType() == SoundInstance.AttenuationType.LINEAR
									? 2.0F / (Math.max(soundInstance.getVolume(), 1.0F) + 2.0F)
									: 0.0F,
							EXTEfx.AL_REVERB_MIN_ROOM_ROLLOFF_FACTOR, EXTEfx.AL_REVERB_MAX_ROOM_ROLLOFF_FACTOR));
			EXTEfx
				.alEffecti(id, EXTEfx.AL_REVERB_DECAY_HFLIMIT,
					MathHelper
						.clamp(data.getDecayHFLimit(client, soundInstance), EXTEfx.AL_REVERB_MIN_DECAY_HFLIMIT,
							EXTEfx.AL_REVERB_MAX_DECAY_HFLIMIT));
			EXTEfx.alAuxiliaryEffectSloti(slot, EXTEfx.AL_EFFECTSLOT_EFFECT, id);
			EXTEfx.alAuxiliaryEffectSlotf(slot, EXTEfx.AL_EFFECTSLOT_GAIN, 1);

			return true;
		}

		return false;
	}

	public static void update(SoundInstance soundInstance, int sourceID) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (!(client == null || client.world == null)) {
			Optional<SoundEffects> soundEffects = LookupGrabber
				.snatch(client.world.getRegistryManager().getLookup(SoundEffects.SOUND_EFFECTS_KEY).get(),
					RegistryKey.of(SoundEffects.SOUND_EFFECTS_KEY, client.world.getRegistryKey().getValue()));

			if (soundEffects.isPresent()) {
				Optional<ReverbEffect> reverb = soundEffects.get().getReverb();

				if (reverb.isPresent()) {

					if (!reverb.get().shouldIgnore(soundInstance.getId())) {

						for (int i = 0; i < 2; i++) {
							AL11.alSourcei(sourceID, EXTEfx.AL_DIRECT_FILTER, 0);
							AL11
								.alSource3i(sourceID, EXTEfx.AL_AUXILIARY_SEND_FILTER,
									update(soundInstance, reverb.get()) ? slot : 0, 0, 0);
							int error = AL11.alGetError();

							if (error == AL11.AL_NO_ERROR) {
								break;
							} else {
								LOGGER.warn("OpenAl Error {}", error);
							}

						}

					}

				}

			}

		}

	}

}
