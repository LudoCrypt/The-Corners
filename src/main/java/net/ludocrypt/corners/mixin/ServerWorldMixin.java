package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.corners.init.CornerWorlds;
import net.minecraft.network.packet.s2c.play.GameStateUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.ServerWorldProperties;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

	@Shadow
	@Final
	private ServerWorldProperties worldProperties;

	@Inject(method = "Lnet/minecraft/server/world/ServerWorld;tickWeather()V", at = @At("HEAD"))
	private void corners$tickWeather(CallbackInfo ci) {

		ServerWorld world = ((ServerWorld) (Object) this);

		if (world.getRegistryKey().equals(CornerWorlds.HOARY_CROSSROADS_KEY)) {

			this.worldProperties.setRainTime(0);
			this.worldProperties.setRaining(true);
			this.worldProperties.setThunderTime(0);
			this.worldProperties.setThundering(false);

			world.setRainGradient(2.0F);

			if (!world.isRaining()) {
				world
					.getServer()
					.getPlayerManager()
					.sendToDimension(new GameStateUpdateS2CPacket(GameStateUpdateS2CPacket.RAIN_GRADIENT_CHANGED, 2.0F),
						world.getRegistryKey());
				world
					.getServer()
					.getPlayerManager()
					.sendToDimension(new GameStateUpdateS2CPacket(GameStateUpdateS2CPacket.RAIN_STARTED, 0.0F),
						world.getRegistryKey());
			}

		} else if (world.getRegistryKey().getValue().getNamespace().equals("corners")) {

			this.worldProperties.setRainTime(0);
			this.worldProperties.setRaining(false);
			this.worldProperties.setThunderTime(0);
			this.worldProperties.setThundering(false);

			world.setRainGradient(0.0F);

			if (world.isRaining()) {
				world
					.getServer()
					.getPlayerManager()
					.sendToDimension(new GameStateUpdateS2CPacket(GameStateUpdateS2CPacket.RAIN_GRADIENT_CHANGED, 0.0F),
						((ServerWorld) (Object) this).getRegistryKey());
				world
					.getServer()
					.getPlayerManager()
					.sendToDimension(new GameStateUpdateS2CPacket(GameStateUpdateS2CPacket.RAIN_STOPPED, 0.0F),
						world.getRegistryKey());
			}

		}

	}

}
