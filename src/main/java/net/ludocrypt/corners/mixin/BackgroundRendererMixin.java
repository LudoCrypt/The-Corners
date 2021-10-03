package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.ludocrypt.corners.init.CornerWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

	@ModifyVariable(method = "render", at = @At("STORE"), ordinal = 0)
	private static double corners$modifySkyColor(double in) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world.getRegistryKey().equals(CornerWorld.YEARNING_CANAL_WORLD_REGISTRY_KEY)) {
			return 1.0;
		} else if (client.world.getRegistryKey().equals(CornerWorld.COMMUNAL_CORRIDORS_WORLD_REGISTRY_KEY)) {
			return 1.0;
		}
		return in;
	}

}
