package net.ludocrypt.corners.client.sound;

import java.util.Optional;

import org.apache.commons.lang3.mutable.Mutable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.corners.entity.DimensionalPaintingEntity;
import net.ludocrypt.corners.init.CornerSoundEvents;
import net.ludocrypt.limlib.api.sound.LiminalTravelSound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;

public class CornerTravelSound extends LiminalTravelSound {

	public static final Codec<CornerTravelSound> CODEC = RecordCodecBuilder.create((instance) -> instance.stable(new CornerTravelSound()));

	@Override
	public void hookSound(ServerWorld from, ServerWorld to, Mutable<Optional<SoundEvent>> mutable) {
		if (DimensionalPaintingEntity.comingFromPainting) {
			mutable.setValue(Optional.of(CornerSoundEvents.PAINTING_PORTAL_TRAVEL));
		}

		DimensionalPaintingEntity.comingFromPainting = false;
	}

	@Override
	public Codec<? extends LiminalTravelSound> getCodec() {
		return CODEC;
	}

	@Override
	public int priority() {
		return 600;
	}

}
