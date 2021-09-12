package net.ludocrypt.corners.mixin;

import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.init.CornerShaderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	@Final
	private ResourceManager resourceManager;

	@Unique
	private final HashMap<Identifier, ShaderEffect> shaderEffects = new HashMap<Identifier, ShaderEffect>();

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawEntityOutlinesFramebuffer()V", shift = Shift.AFTER))
	private void corners$renderShader(float tickDelta, long nanoTime, boolean renderLevel, CallbackInfo ci) {
		ShaderEffect shader = this.corners$getShader(CornerShaderRegistry.getCurrent(client.world.getRegistryKey()));
		if (shader != null) {
			RenderSystem.disableBlend();
			RenderSystem.disableDepthTest();
			RenderSystem.enableTexture();
			RenderSystem.resetTextureMatrix();
			shader.render(tickDelta);
			MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
			RenderSystem.disableBlend();
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			RenderSystem.enableDepthTest();
		}
	}

	@Inject(method = "reload", at = @At("HEAD"))
	private void corners$reload(ResourceManager manager, CallbackInfo ci) {
		shaderEffects.forEach((id, shaderEffect) -> shaderEffect.close());
		shaderEffects.clear();
	}

	@Unique
	private ShaderEffect corners$getShader(Identifier id) {

		if (shaderEffects.containsKey(id)) {
			return shaderEffects.get(id);
		}

		try {
			ShaderEffect shader = new ShaderEffect(this.client.getTextureManager(), this.resourceManager, this.client.getFramebuffer(), id);
			shader.setupDimensions(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
			shaderEffects.put(id, shader);
			return shader;
		} catch (IOException var3) {
			TheCorners.LOGGER.warn((String) "Failed to load shader: {}", (Object) id, (Object) var3);
			return null;
		} catch (JsonSyntaxException var4) {
			TheCorners.LOGGER.warn((String) "Failed to parse shader: {}", (Object) id, (Object) var4);
			return null;
		}
	}

}
