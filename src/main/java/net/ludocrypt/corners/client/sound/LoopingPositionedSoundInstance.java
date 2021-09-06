package net.ludocrypt.corners.client.sound;

import java.util.concurrent.atomic.AtomicBoolean;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.corners.init.CornerBlocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.Source;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LoopingPositionedSoundInstance extends PositionedSoundInstance implements StoreSourceExtraInfoAtHead, StoreSourceExtraInfoAtTail, UnnaturalStopper {

	public static boolean isReloading = false;

	private final World world;
	private final BlockPos pos;
	private final SoundEvent soundEvent;
	private boolean stoppingUnnaturally = false;

	public LoopingPositionedSoundInstance(World world, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch, double x, double y, double z) {
		super(sound, category, volume, pitch, x, y, z);
		this.world = world;
		this.pos = pos;
		this.soundEvent = sound;
	}

	@Override
	public void tickAtHead(Source source, SoundInstance soundInstance, Sound sound, int pointer, AtomicBoolean playing, int bufferSize, AudioStream stream, CallbackInfo ci) {
		if (!this.world.getBlockState(pos).isOf(CornerBlocks.WOODEN_RADIO)) {
			this.setStoppingUnnaturally();
			source.stop();
		}
		if (MinecraftClient.getInstance().isPaused()) {
			source.pause();
		} else {
			source.resume();
		}
	}

	@Override
	public void closeAtHead(Source source, SoundInstance soundInstance, Sound sound, int pointer, AtomicBoolean playing, int bufferSize, AudioStream stream, CallbackInfo ci) {
		if (this.isStoppingNaturally()) {
			if (!isReloading) {
				play(world, pos, soundEvent, category, volume, pitch, x, y, z);
			}
		}
	}

	public static void play(World world, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch, double x, double y, double z) {
		MinecraftClient client = MinecraftClient.getInstance();
		LoopingPositionedSoundInstance soundInstance = new LoopingPositionedSoundInstance(world, pos, sound, category, volume, pitch, x, y, z);
		client.getSoundManager().play(soundInstance, 0);
	}

	public static void playNormalLoop(World world, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch, double x, double y, double z) {
		MinecraftClient client = MinecraftClient.getInstance();
		LoopingPositionedSoundInstance soundInstance = new LoopingPositionedSoundInstance(world, pos, sound, category, volume, pitch, x, y, z);
		soundInstance.repeat = true;
		soundInstance.repeatDelay = 0;
		client.getSoundManager().play(soundInstance);
	}

	@Override
	public boolean isStoppingUnnaturally() {
		return stoppingUnnaturally;
	}

	@Override
	public void forceStoppingUnnaturally(boolean unnatural) {
		this.stoppingUnnaturally = unnatural;
	}
}
