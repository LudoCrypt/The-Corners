package net.ludocrypt.corners.util;

import net.minecraft.registry.Holder;
import net.minecraft.sound.SoundEvent;

public class RadioSoundTable {

	private final Holder.Reference<SoundEvent> musicSound;
	private final Holder.Reference<SoundEvent> staticSound;
	private final Holder.Reference<SoundEvent> radioSound;

	public RadioSoundTable(Holder.Reference<SoundEvent> musicSound, Holder.Reference<SoundEvent> staticSound, Holder.Reference<SoundEvent> radioSound) {
		this.musicSound = musicSound;
		this.staticSound = staticSound;
		this.radioSound = radioSound;
	}

	public Holder.Reference<SoundEvent> getMusicSound() {
		return musicSound;
	}

	public Holder.Reference<SoundEvent> getStaticSound() {
		return staticSound;
	}

	public Holder.Reference<SoundEvent> getRadioSound() {
		return radioSound;
	}

}
