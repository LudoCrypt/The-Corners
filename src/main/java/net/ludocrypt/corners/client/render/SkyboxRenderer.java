package net.ludocrypt.corners.client.render;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.limlib.render.special.SpecialModelRenderer;
import net.minecraft.client.render.ShaderProgram;
import net.minecraft.client.util.math.MatrixStack;

public class SkyboxRenderer extends SpecialModelRenderer {

	private final String id;

	public SkyboxRenderer(String id) {
		this.id = id;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void setup(MatrixStack matrices, ShaderProgram shader) {
		for (int i = 0; i < 6; i++) {
			RenderSystem.setShaderTexture(i, TheCorners.id("textures/sky/" + id + "_" + i + ".png"));
		}
	}

}
