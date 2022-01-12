package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import java.util.Optional;

import org.apache.commons.lang3.mutable.Mutable;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.entity.DimensionalPaintingEntity;
import net.ludocrypt.limlib.api.sound.LiminalTravelSound;
import net.ludocrypt.limlib.impl.sound.LiminalTravelSounds;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;

public class CornerSoundEvents {

	// Misc
	public static final SoundEvent PAINTING_PORTAL_TRAVEL = get("misc.portal.painting.travel");

	// Music
	public static final SoundEvent MUSIC_YEARNING_CANAL = get("music.yearning_canal");
	public static final SoundEvent MUSIC_COMMUNAL_CORRIDORS = get("music.communal_corridors");

	// Radio
	public static final SoundEvent RADIO_DEFAULT_STATIC = get("radio.default.static");
	public static final SoundEvent RADIO_YEARNING_CANAL = get("radio.yearning_canal");
	public static final SoundEvent RADIO_COMMUNAL_CORRIDORS = get("radio.communal_corridors");

	// Ambient
	public static final SoundEvent BIOME_LOOP_COMMUNAL_CORRIDORS = get("biome.communal_corridors.loop");

	public static void init() {
		LiminalTravelSounds.register(TheCorners.id("from_corners"), new LiminalTravelSound() {

			@Override
			public void hookSound(ServerWorld from, ServerWorld to, Mutable<Optional<SoundEvent>> mutable) {
				if (DimensionalPaintingEntity.comingFromPainting) {
					mutable.setValue(Optional.of(CornerSoundEvents.PAINTING_PORTAL_TRAVEL));
				}

				DimensionalPaintingEntity.comingFromPainting = false;
			}

			@Override
			public int priority() {
				return 500;
			}
		});
	}

}
