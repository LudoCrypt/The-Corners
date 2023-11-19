package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.corners.init.CornerWorlds;
import net.minecraft.network.packet.s2c.play.GameStateUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

	@Inject(method = "Lnet/minecraft/server/world/ServerWorld;tickWeather()V", at = @At("HEAD"))
	private void corners$tickWeather(CallbackInfo ci) {

		if (((ServerWorld) (Object) this).getRegistryKey().equals(CornerWorlds.HOARY_CROSSROADS_KEY)) {
			((ServerWorld) (Object) this).getServer().getPlayerManager().sendToDimension(new GameStateUpdateS2CPacket(GameStateUpdateS2CPacket.RAIN_GRADIENT_CHANGED, 2.0F),
					((ServerWorld) (Object) this).getRegistryKey());
		}

	}

}
