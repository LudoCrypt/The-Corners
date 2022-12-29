package net.ludocrypt.corners.client.render;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.limlib.render.special.SpecialModelRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.ShaderProgram;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

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

		MinecraftClient client = MinecraftClient.getInstance();
		Camera camera = client.gameRenderer.getCamera();

		Matrix4f matrix = new MatrixStack().peek().getPosition();

		matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
		matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));

		if (shader.getUniform("RotMat") != null) {
			shader.getUniform("RotMat").setMat4x4(matrix);
		}
	}

}
