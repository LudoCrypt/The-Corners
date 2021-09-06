package net.ludocrypt.corners.access;

import org.jetbrains.annotations.Nullable;

import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public interface SoundSystemAccess {

	public void stopSoundsAtPosition(double x, double y, double z, @Nullable Identifier id, @Nullable SoundCategory category);

	public static SoundSystemAccess get(Object obj) {
		return (SoundSystemAccess) obj;
	}

}
