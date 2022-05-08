package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;

@Mixin(SoundManager.class)
public interface SoundManagerAccessor {

	@Accessor
	SoundSystem getSoundSystem();

}
