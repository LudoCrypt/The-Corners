package net.ludocrypt.corners.mixin;

import java.util.Map;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Multimap;

import net.ludocrypt.corners.access.SoundSystemAccess;
import net.ludocrypt.corners.access.SourceAccess;
import net.ludocrypt.corners.client.sound.LoopingPositionedSoundInstance;
import net.ludocrypt.corners.client.sound.StoreSourceExtraInfoAtHead;
import net.ludocrypt.corners.client.sound.UnnaturalStopper;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.Source;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin implements SoundSystemAccess {

	@Shadow
	@Final
	private Multimap<SoundCategory, SoundInstance> sounds;

	@Shadow
	@Final
	private Map<SoundInstance, Channel.SourceManager> sources;

	@Unique
	private Sound playingSound;

	@Unique
	private SoundInstance sound;

	@Override
	public void stopSoundsAtPosition(double x, double y, double z, @Nullable Identifier id, @Nullable SoundCategory category) {
		Consumer<SoundInstance> consumer = (soundInstance) -> {
			if ((id != null ? soundInstance.getId().equals(id) : true) && (soundInstance.getX() == x) && (soundInstance.getY() == y) && (soundInstance.getZ() == z)) {
				if (soundInstance instanceof UnnaturalStopper unnaturalStopper) {
					unnaturalStopper.setStoppingUnnaturally();
				}
				this.stop(soundInstance);
			}
		};

		if (category != null) {
			this.sounds.get(category).forEach(consumer);
		} else {
			this.sounds.forEach((soundCategory, soundInstance) -> consumer.accept(soundInstance));
		}
	}

	@Inject(method = "stop", at = @At("HEAD"))
	private void corners$stop(CallbackInfo ci) {
		LoopingPositionedSoundInstance.isReloading = true;
	}

	@Inject(method = "start", at = @At("TAIL"))
	private void corners$start(CallbackInfo ci) {
		LoopingPositionedSoundInstance.isReloading = false;
	}

	@Inject(method = "play", at = @At("HEAD"))
	private void corners$play$storeSoundInstance(SoundInstance sound, CallbackInfo ci) {
		this.sound = sound;
	}

	@ModifyVariable(method = "play", at = @At("STORE"), ordinal = 0)
	private Sound corners$play$storePlayingSound(Sound in) {
		this.playingSound = in;
		return in;
	}

	@ModifyArg(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/Channel$SourceManager;run(Ljava/util/function/Consumer;)V", ordinal = 0), index = 0)
	private Consumer<Source> corners$play$modifyRunMethod(Consumer<Source> in) {
		return (source) -> {
			in.accept(source);
			if (sound instanceof StoreSourceExtraInfoAtHead) {
				SourceAccess.get(source).setSound(playingSound);
				SourceAccess.get(source).setSoundInstance(sound);
			}
		};
	}

	@Shadow
	public abstract void stop(SoundInstance sound);

}
