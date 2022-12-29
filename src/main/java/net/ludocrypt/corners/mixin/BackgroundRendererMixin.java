package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.ludocrypt.corners.init.CornerWorlds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.MathHelper;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {

	@Inject(method = "Lnet/minecraft/client/render/BackgroundRenderer;applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZF)V", at = @At("TAIL"))
	private static void corners$applyFog(Camera camera, FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		float fogStart = RenderSystem.getShaderFogStart();
		float fogEnd = RenderSystem.getShaderFogEnd();

		if (client.world.getRegistryKey().equals(CornerWorlds.HOARY_CROSSROADS_KEY)) {
			fogStart = fogStart / 2;
			fogEnd = fogEnd / 2;
			float cameraHeight = (float) (camera.getPos().getY() - client.world.getBottomY());
			float fogScalar = (float) MathHelper.clamp(Math.atan(((cameraHeight - 263.0F) * Math.tan(1)) / 263.0F) + 1, 0.0F, 1.0F);
			RenderSystem.setShaderFogStart(fogStart * fogScalar);
			RenderSystem.setShaderFogEnd(fogEnd * fogScalar);
		}
	}

}
