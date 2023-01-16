package net.ludocrypt.corners.init;

import net.ludocrypt.corners.TheCorners;
import net.minecraft.registry.Holder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class CornerSoundEvents {

	// Misc
	public static final Holder.Reference<SoundEvent> PAINTING_PORTAL_TRAVEL = get("misc.portal.painting.travel");

	// Music
	public static final Holder.Reference<SoundEvent> MUSIC_YEARNING_CANAL = get("music.yearning_canal");
	public static final Holder.Reference<SoundEvent> MUSIC_COMMUNAL_CORRIDORS = get("music.communal_corridors");
	public static final Holder.Reference<SoundEvent> MUSIC_HOARY_CROSSROADS = get("music.hoary_crossroads");

	// Radio
	public static final Holder.Reference<SoundEvent> RADIO_DEFAULT_STATIC = get("radio.default.static");

	public static final Holder.Reference<SoundEvent> RADIO_YEARNING_CANAL = get("radio.yearning_canal");
	public static final Holder.Reference<SoundEvent> RADIO_COMMUNAL_CORRIDORS = get("radio.communal_corridors");
	public static final Holder.Reference<SoundEvent> RADIO_HOARY_CROSSROADS = get("radio.hoary_crossroads");

	public static final Holder.Reference<SoundEvent> RADIO_YEARNING_CANAL_STATIC = get("radio.yearning_canal.static");
	public static final Holder.Reference<SoundEvent> RADIO_COMMUNAL_CORRIDORS_STATIC = get("radio.communal_corridors.static");
	public static final Holder.Reference<SoundEvent> RADIO_HOARY_CROSSROADS_STATIC = get("radio.hoary_crossroads.static");

	public static final Holder.Reference<SoundEvent> RADIO_YEARNING_CANAL_MUSIC = get("radio.yearning_canal.music");
	public static final Holder.Reference<SoundEvent> RADIO_COMMUNAL_CORRIDORS_MUSIC = get("radio.communal_corridors.music");
	public static final Holder.Reference<SoundEvent> RADIO_HOARY_CROSSROADS_MUSIC = get("radio.hoary_crossroads.music");

	// Ambient
	public static final Holder.Reference<SoundEvent> BIOME_LOOP_COMMUNAL_CORRIDORS = get("biome.communal_corridors.loop");
	public static final Holder.Reference<SoundEvent> BIOME_LOOP_HOARY_CROSSROADS = get("biome.hoary_crossroads.loop");

	public static void init() {

	}

	public static Holder.Reference<SoundEvent> get(String id) {
		return Registry.m_jktnfzok(Registries.SOUND_EVENT, TheCorners.id(id), SoundEvent.createVariableRangeEvent(TheCorners.id(id)));
	}

}
