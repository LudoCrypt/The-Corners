package net.ludocrypt.corners.client.sound;

import java.util.concurrent.atomic.AtomicBoolean;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.Source;

public interface StoreSourceExtraInfoAtTail {

	public default void closeAtTail(Source source, SoundInstance soundInstance, Sound sound, int pointer, AtomicBoolean playing, int bufferSize, AudioStream stream, CallbackInfo ci) {

	}

	public default void tickAtTail(Source source, SoundInstance soundInstance, Sound sound, int pointer, AtomicBoolean playing, int bufferSize, AudioStream stream, CallbackInfo ci) {

	}

	public default void stopAtTail(Source source, SoundInstance soundInstance, Sound sound, int pointer, AtomicBoolean playing, int bufferSize, AudioStream stream, CallbackInfo ci) {

	}

	public default void resumeAtTail(Source source, SoundInstance soundInstance, Sound sound, int pointer, AtomicBoolean playing, int bufferSize, AudioStream stream, CallbackInfo ci) {

	}

	public default void pauseAtTail(Source source, SoundInstance soundInstance, Sound sound, int pointer, AtomicBoolean playing, int bufferSize, AudioStream stream, CallbackInfo ci) {

	}

	public default void playAtTail(Source source, SoundInstance soundInstance, Sound sound, int pointer, AtomicBoolean playing, int bufferSize, AudioStream stream, CallbackInfo ci) {

	}

}
