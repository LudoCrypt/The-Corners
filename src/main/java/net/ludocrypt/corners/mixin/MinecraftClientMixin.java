package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.glfw.Window;

import net.ludocrypt.corners.config.CornerConfig;
import net.ludocrypt.corners.init.CornerSoundEvents;
import net.ludocrypt.corners.init.CornerWorlds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.MusicSound;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Shadow
	public ClientPlayerEntity player;
	@Shadow
	public ClientWorld world;
	@Final
	@Shadow
	private Window window;

	@Inject(method = "getMusic", at = @At("HEAD"), cancellable = true)
	private void corners$getMusic(CallbackInfoReturnable<MusicSound> ci) {

		if (this.player != null) {

			if (world.getRegistryKey().equals(CornerWorlds.COMMUNAL_CORRIDORS_KEY)) {

				if (CornerConfig.get().christmas.isChristmas()) {
					ci.setReturnValue(new MusicSound(CornerSoundEvents.MUSIC_COMMUNAL_CORRIDORS_CHRISTMAS, 3000, 8000, true));
				} else {
					ci.setReturnValue(new MusicSound(CornerSoundEvents.MUSIC_COMMUNAL_CORRIDORS, 3000, 8000, true));
				}

			}

		}

	}

}
