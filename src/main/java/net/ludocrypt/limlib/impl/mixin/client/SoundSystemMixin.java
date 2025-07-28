package net.ludocrypt.limlib.impl.mixin.client;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.Multimap;

import net.ludocrypt.limlib.api.effects.sound.distortion.DistortionFilter;
import net.ludocrypt.limlib.api.effects.sound.reverb.ReverbFilter;
import net.ludocrypt.limlib.impl.access.SoundSystemAccess;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin implements SoundSystemAccess {

	@Shadow
	@Final
	private Map<SoundInstance, Channel.SourceManager> sources;

	@Shadow
	@Final
	private Multimap<SoundCategory, SoundInstance> sounds;

	@Override
	public void stopSoundsAtPosition(double x, double y, double z, @Nullable Identifier id,
			@Nullable SoundCategory category) {
		Consumer<SoundInstance> consumer = (soundInstance) -> {

			if ((id != null ? soundInstance.getId().equals(id)
					: true) && (soundInstance.getX() == x) && (soundInstance.getY() == y) && (soundInstance.getZ() == z)) {
				this.stop(soundInstance);
			}

		};

		if (category != null) {
			this.sounds.get(category).forEach(consumer);
		} else {
			this.sounds.forEach((soundCategory, soundInstance) -> consumer.accept(soundInstance));
		}

	}

	@Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getSoundVolume(Lnet/minecraft/sound/SoundCategory;)F", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	public void limlib$tick(CallbackInfo ci, Iterator<?> iterator, Map.Entry<?, ?> entry,
			Channel.SourceManager sourceManager, SoundInstance soundInstance) {
		sourceManager.run(source -> ReverbFilter.update(soundInstance, ((SourceAccessor) source).getPointer()));
		sourceManager.run(source -> DistortionFilter.update(soundInstance, ((SourceAccessor) source).getPointer()));
	}

	@Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/Channel$SourceManager;run(Ljava/util/function/Consumer;)V", ordinal = 0, shift = Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	public void limlib$play(SoundInstance soundInstance, CallbackInfo ci, WeightedSoundSet weightedSoundSet,
			Identifier identifier, Sound sound, float f, float g, SoundCategory soundCategory, float h, float i,
			SoundInstance.AttenuationType attenuationType, boolean bl, Vec3d vec3d, boolean bl3, boolean bl4,
			CompletableFuture<?> completableFuture, Channel.SourceManager sourceManager) {
		sourceManager.run(source -> ReverbFilter.update(soundInstance, ((SourceAccessor) source).getPointer()));
		sourceManager.run(source -> DistortionFilter.update(soundInstance, ((SourceAccessor) source).getPointer()));
	}

	@Inject(method = "reloadSounds()V", at = @At("TAIL"))
	public void limlib$reloadSounds(CallbackInfo ci) {
		ReverbFilter.update();
		DistortionFilter.update();
	}

	@Shadow
	public abstract void stop(SoundInstance sound);

}
