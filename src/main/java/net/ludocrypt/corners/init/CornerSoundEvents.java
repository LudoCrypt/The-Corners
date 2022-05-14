package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import net.minecraft.sound.SoundEvent;

public class CornerSoundEvents {

	// Misc
	public static final SoundEvent PAINTING_PORTAL_TRAVEL = get("misc.portal.painting.travel");

	// Music
	public static final SoundEvent MUSIC_YEARNING_CANAL = get("music.yearning_canal");
	public static final SoundEvent MUSIC_COMMUNAL_CORRIDORS = get("music.communal_corridors");
	public static final SoundEvent MUSIC_HOARY_CROSSROADS = get("music.hoary_crossroads");

	// Radio
	public static final SoundEvent RADIO_DEFAULT_STATIC = get("radio.default.static");
	public static final SoundEvent RADIO_YEARNING_CANAL = get("radio.yearning_canal");
	public static final SoundEvent RADIO_COMMUNAL_CORRIDORS = get("radio.communal_corridors");
	public static final SoundEvent RADIO_HOARY_CROSSROADS = get("radio.hoary_crossroads");

	// Ambient
	public static final SoundEvent BIOME_LOOP_COMMUNAL_CORRIDORS = get("biome.communal_corridors.loop");
	public static final SoundEvent BIOME_LOOP_HOARY_CROSSROADS = get("biome.hoary_crossroads.loop");

	public static void init() {
	}

}
