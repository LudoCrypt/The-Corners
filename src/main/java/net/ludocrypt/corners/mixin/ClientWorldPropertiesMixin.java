package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.corners.init.CornerWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.Properties.class)
public class ClientWorldPropertiesMixin {

	@Inject(method = "getSkyDarknessHeight", at = @At("HEAD"), cancellable = true)
	private void corners$getSkyDarknessHeight(CallbackInfoReturnable<Double> ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world.getRegistryKey().equals(CornerWorld.YEARNING_CANAL_WORLD_REGISTRY_KEY)) {
			ci.setReturnValue(-32.0D);
		}
	}

	@Inject(method = "getHorizonShadingRatio", at = @At("HEAD"), cancellable = true)
	private void corners$getHorizonShadingRatio(CallbackInfoReturnable<Double> ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world.getRegistryKey().equals(CornerWorld.YEARNING_CANAL_WORLD_REGISTRY_KEY)) {
			ci.setReturnValue(16.0D);
		}
	}

}
