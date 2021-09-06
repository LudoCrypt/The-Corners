package net.ludocrypt.corners.access;

import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;

public interface SourceAccess {

	public SoundInstance getSoundInstance();

	public void setSoundInstance(SoundInstance sound);

	public Sound getSound();

	public void setSound(Sound sound);

	public static SourceAccess get(Object obj) {
		return (SourceAccess) obj;
	}

}
