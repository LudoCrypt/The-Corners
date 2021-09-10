package net.ludocrypt.corners.client.sound;

import net.ludocrypt.corners.init.CornerBlocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LoopingPositionedSoundInstance extends PositionedSoundInstance implements TickableSoundInstance {

	private final World world;
	private final BlockPos pos;
	private boolean isDone = false;

	public LoopingPositionedSoundInstance(World world, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch, double x, double y, double z) {
		super(sound, category, volume, pitch, x, y, z);
		this.world = world;
		this.pos = pos;
	}

	public static void play(World world, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch, double x, double y, double z) {
		MinecraftClient client = MinecraftClient.getInstance();
		LoopingPositionedSoundInstance soundInstance = new LoopingPositionedSoundInstance(world, pos, sound, category, volume, pitch, x, y, z);
		soundInstance.repeat = true;
		soundInstance.repeatDelay = 0;
		client.getSoundManager().play(soundInstance);
	}

	@Override
	public boolean isDone() {
		return this.isDone;
	}

	@Override
	public void tick() {
		if (!this.world.getBlockState(pos).isOf(CornerBlocks.WOODEN_RADIO)) {
			this.isDone = true;
		}
	}

}
