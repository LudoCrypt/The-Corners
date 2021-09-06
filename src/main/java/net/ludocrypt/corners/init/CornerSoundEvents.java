package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import net.minecraft.sound.SoundEvent;

public class CornerSoundEvents {

	// Misc
	public static final SoundEvent PAINTING_PORTAL_TRAVEL = get("misc.portal.painting.travel");

	// Music
	public static final SoundEvent MUSIC_YEARNING_CANAL = get("music.yearning_canal");

	// Radio
	public static final SoundEvent UNDERLYING_STATIC = get("radio.underlying.static");
	public static final SoundEvent RADIO_DEFAULT_STATIC = get("radio.default.static");
	public static final SoundEvent RADIO_YEARNING_CANAL = get("radio.yearning_canal");

	public static void init() {

	}

}
