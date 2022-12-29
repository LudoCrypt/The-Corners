package net.ludocrypt.corners.util;

import net.minecraft.sound.SoundEvent;

public class RadioSoundTable {

	private final SoundEvent musicSound;
	private final SoundEvent staticSound;
	private final SoundEvent radioSound;

	public RadioSoundTable(SoundEvent musicSound, SoundEvent staticSound, SoundEvent radioSound) {
		this.musicSound = musicSound;
		this.staticSound = staticSound;
		this.radioSound = radioSound;
	}

	public SoundEvent getMusicSound() {
		return musicSound;
	}

	public SoundEvent getStaticSound() {
		return staticSound;
	}

	public SoundEvent getRadioSound() {
		return radioSound;
	}

}
