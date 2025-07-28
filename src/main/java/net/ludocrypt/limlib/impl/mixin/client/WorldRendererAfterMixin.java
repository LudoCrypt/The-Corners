package net.ludocrypt.limlib.impl.mixin.client;

import java.util.Optional;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.limlib.api.effects.LookupGrabber;
import net.ludocrypt.limlib.api.skybox.Skybox;
import net.ludocrypt.limlib.impl.bridge.IrisBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.RegistryKey;

@Mixin(value = WorldRenderer.class, priority = 1050)
public abstract class WorldRendererAfterMixin {

	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;)V", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
	private void limlib$render$clear(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline,
			Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix,
			CallbackInfo ci) {

		if (IrisBridge.IRIS_LOADED) {

			if (IrisBridge.areShadersInUse()) {
				MinecraftClient client = MinecraftClient.getInstance();

				Optional<Skybox> sky = LookupGrabber
					.snatch(client.world.getRegistryManager().getLookup(Skybox.SKYBOX_KEY).get(),
						RegistryKey.of(Skybox.SKYBOX_KEY, client.world.getRegistryKey().getValue()));

				if (sky.isPresent()) {
					sky.get().renderSky(((WorldRenderer) (Object) this), client, matrices, positionMatrix, tickDelta);
				}

			}

		}

	}

}
