package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.ludocrypt.corners.init.CornerWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

	@ModifyVariable(method = "render", at = @At(value = "STORE", ordinal = 2), index = 7)
	private static float corners$modifySkyColor(float in) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world.getRegistryKey().equals(CornerWorld.YEARNING_CANAL.getWorldKey())) {
			return 1.0F;
		} else if (client.world.getRegistryKey().equals(CornerWorld.COMMUNAL_CORRIDORS.getWorldKey())) {
			return 1.0F;
		} else if (client.world.getRegistryKey().equals(CornerWorld.HOARY_CROSSROADS.getWorldKey())) {
			return 1.0F;
		}
		return in;
	}

}
