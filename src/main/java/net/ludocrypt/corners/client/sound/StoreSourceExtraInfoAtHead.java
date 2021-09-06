package net.ludocrypt.corners.client.sound;

import java.util.concurrent.atomic.AtomicBoolean;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.Source;

public interface StoreSourceExtraInfoAtHead {

	public default void closeAtHead(Source source, SoundInstance soundInstance, Sound sound, int pointer, AtomicBoolean playing, int bufferSize, AudioStream stream, CallbackInfo ci) {

	}

	public default void tickAtHead(Source source, SoundInstance soundInstance, Sound sound, int pointer, AtomicBoolean playing, int bufferSize, AudioStream stream, CallbackInfo ci) {

	}

	public default void stopAtHead(Source source, SoundInstance soundInstance, Sound sound, int pointer, AtomicBoolean playing, int bufferSize, AudioStream stream, CallbackInfo ci) {

	}

	public default void resumeAtHead(Source source, SoundInstance soundInstance, Sound sound, int pointer, AtomicBoolean playing, int bufferSize, AudioStream stream, CallbackInfo ci) {

	}

	public default void pauseAtHead(Source source, SoundInstance soundInstance, Sound sound, int pointer, AtomicBoolean playing, int bufferSize, AudioStream stream, CallbackInfo ci) {

	}

	public default void playAtHead(Source source, SoundInstance soundInstance, Sound sound, int pointer, AtomicBoolean playing, int bufferSize, AudioStream stream, CallbackInfo ci) {

	}

}
