package net.ludocrypt.limlib.impl.shader;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.ludocrypt.limlib.impl.Limlib;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class PostProcesser {

	private final Identifier location;
	protected ShaderEffect shader;
	private boolean loaded;

	public PostProcesser(Identifier location) {
		this.location = location;
	}

	public void init(ResourceManager resourceManager) {

		try {
			this.release();
			MinecraftClient client = MinecraftClient.getInstance();
			this.shader = parseShader(resourceManager, client, this.location);
			this.shader.setupDimensions(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());
		} catch (IOException e) {
			this.loaded = true;
			Limlib.LOGGER.error("Could not create screen shader {}", this.getLocation(), e);
		}

	}

	protected ShaderEffect parseShader(ResourceManager resourceManager, MinecraftClient mc, Identifier location)
			throws IOException {
		return new ShaderEffect(mc.getTextureManager(), resourceManager, mc.getFramebuffer(), location);
	}

	public void release() {

		if (this.isInitialized()) {

			try {
				assert this.shader != null;
				this.shader.close();
				this.shader = null;
			} catch (Exception e) {
				throw new RuntimeException("Failed to release shader " + this.location, e);
			}

		}

		this.loaded = false;
	}

	public void render(float tickDelta) {
		ShaderEffect shader = this.getShaderEffect();

		if (shader != null) {
			RenderSystem.disableBlend();
			RenderSystem.disableDepthTest();
			RenderSystem.resetTextureMatrix();
			shader.render(tickDelta);
			MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
			RenderSystem.disableBlend();
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			RenderSystem.enableDepthTest();
		}

	}

	public Identifier getLocation() {
		return location;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public boolean isInitialized() {
		return this.shader != null;
	}

	public ShaderEffect getShaderEffect() {

		if (!this.isInitialized() && !this.isLoaded()) {
			this.init(MinecraftClient.getInstance().getResourceManager());
		}

		return this.shader;
	}

}
