package net.ludocrypt.corners.mixin;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.corners.access.SourceAccess;
import net.ludocrypt.corners.client.sound.StoreSourceExtraInfoAtHead;
import net.ludocrypt.corners.client.sound.StoreSourceExtraInfoAtTail;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.Source;

@Mixin(Source.class)
public class SourceMixin implements SourceAccess {

	@Unique
	private SoundInstance soundInstance;

	@Unique
	private Sound sound;

	@Shadow
	@Final
	private int pointer;

	@Shadow
	@Final
	private AtomicBoolean playing;

	@Shadow
	@Final
	private int bufferSize;

	@Nullable
	@Shadow
	private AudioStream stream;

	@Inject(method = "close", at = @At("HEAD"), cancellable = true)
	private void corners$close$head(CallbackInfo ci) {
		if (soundInstance instanceof StoreSourceExtraInfoAtHead extraInfo) {
			extraInfo.closeAtHead(((Source) (Object) this), soundInstance, sound, pointer, playing, bufferSize, stream, ci);
		}
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void corners$tick$head(CallbackInfo ci) {
		if (soundInstance instanceof StoreSourceExtraInfoAtHead extraInfo) {
			extraInfo.tickAtHead(((Source) (Object) this), soundInstance, sound, pointer, playing, bufferSize, stream, ci);
		}
	}

	@Inject(method = "stop", at = @At("HEAD"), cancellable = true)
	private void corners$stop$head(CallbackInfo ci) {
		if (soundInstance instanceof StoreSourceExtraInfoAtHead extraInfo) {
			extraInfo.stopAtHead(((Source) (Object) this), soundInstance, sound, pointer, playing, bufferSize, stream, ci);
		}
	}

	@Inject(method = "resume", at = @At("HEAD"), cancellable = true)
	private void corners$resume$head(CallbackInfo ci) {
		if (soundInstance instanceof StoreSourceExtraInfoAtHead extraInfo) {
			extraInfo.resumeAtHead(((Source) (Object) this), soundInstance, sound, pointer, playing, bufferSize, stream, ci);
		}
	}

	@Inject(method = "pause", at = @At("HEAD"), cancellable = true)
	private void corners$pause$head(CallbackInfo ci) {
		if (soundInstance instanceof StoreSourceExtraInfoAtHead extraInfo) {
			extraInfo.pauseAtHead(((Source) (Object) this), soundInstance, sound, pointer, playing, bufferSize, stream, ci);
		}
	}

	@Inject(method = "play", at = @At("HEAD"), cancellable = true)
	private void corners$play$head(CallbackInfo ci) {
		if (soundInstance instanceof StoreSourceExtraInfoAtHead extraInfo) {
			extraInfo.playAtHead(((Source) (Object) this), soundInstance, sound, pointer, playing, bufferSize, stream, ci);
		}
	}

	@Inject(method = "close", at = @At("TAIL"), cancellable = true)
	private void corners$close$tail(CallbackInfo ci) {
		if (soundInstance instanceof StoreSourceExtraInfoAtTail extraInfo) {
			extraInfo.closeAtTail(((Source) (Object) this), soundInstance, sound, pointer, playing, bufferSize, stream, ci);
		}
	}

	@Inject(method = "tick", at = @At("TAIL"), cancellable = true)
	private void corners$tick$tail(CallbackInfo ci) {
		if (soundInstance instanceof StoreSourceExtraInfoAtTail extraInfo) {
			extraInfo.tickAtTail(((Source) (Object) this), soundInstance, sound, pointer, playing, bufferSize, stream, ci);
		}
	}

	@Inject(method = "stop", at = @At("TAIL"), cancellable = true)
	private void corners$stop$tail(CallbackInfo ci) {
		if (soundInstance instanceof StoreSourceExtraInfoAtTail extraInfo) {
			extraInfo.stopAtTail(((Source) (Object) this), soundInstance, sound, pointer, playing, bufferSize, stream, ci);
		}
	}

	@Inject(method = "resume", at = @At("TAIL"), cancellable = true)
	private void corners$resume$tail(CallbackInfo ci) {
		if (soundInstance instanceof StoreSourceExtraInfoAtTail extraInfo) {
			extraInfo.resumeAtTail(((Source) (Object) this), soundInstance, sound, pointer, playing, bufferSize, stream, ci);
		}
	}

	@Inject(method = "pause", at = @At("TAIL"), cancellable = true)
	private void corners$pause$tail(CallbackInfo ci) {
		if (soundInstance instanceof StoreSourceExtraInfoAtTail extraInfo) {
			extraInfo.pauseAtTail(((Source) (Object) this), soundInstance, sound, pointer, playing, bufferSize, stream, ci);
		}
	}

	@Inject(method = "play", at = @At("TAIL"), cancellable = true)
	private void corners$play$tail(CallbackInfo ci) {
		if (soundInstance instanceof StoreSourceExtraInfoAtTail extraInfo) {
			extraInfo.playAtTail(((Source) (Object) this), soundInstance, sound, pointer, playing, bufferSize, stream, ci);
		}
	}

	@Override
	public SoundInstance getSoundInstance() {
		return this.soundInstance;
	}

	@Override
	public void setSoundInstance(SoundInstance sound) {
		this.soundInstance = sound;
	}

	@Override
	public Sound getSound() {
		return this.sound;
	}

	@Override
	public void setSound(Sound sound) {
		this.sound = sound;
	}

}
