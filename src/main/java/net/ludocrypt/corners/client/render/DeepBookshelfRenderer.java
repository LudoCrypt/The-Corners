package net.ludocrypt.corners.client.render;

import org.joml.Matrix4f;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import com.mojang.blaze3d.systems.RenderSystem;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.mixin.GameRendererAccessor;
import net.ludocrypt.specialmodels.api.SpecialModelRenderer;
import net.ludocrypt.specialmodels.impl.render.MutableQuad;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.ShaderProgram;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;

public class DeepBookshelfRenderer extends SpecialModelRenderer {

	@Override
	@ClientOnly
	public void setup(MatrixStack matrices, Matrix4f viewMatrix, Matrix4f positionMatrix, float tickDelta, ShaderProgram shader) {
		RenderSystem.polygonOffset(-3.0F, -3.0F);
		RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
		RenderSystem.setShaderTexture(1, TheCorners.id("textures/block/deep_bookshelf_interior.png"));

		MinecraftClient client = MinecraftClient.getInstance();
		Camera camera = client.gameRenderer.getCamera();

		MatrixStack matrixStack = new MatrixStack();

		((GameRendererAccessor) client.gameRenderer).callBobViewWhenHurt(matrixStack, tickDelta);
		if (client.options.getBobView().get()) {
			((GameRendererAccessor) client.gameRenderer).callBobView(matrixStack, tickDelta);
		}

		MatrixStack basicStack = new MatrixStack();
		basicStack.multiplyMatrix(client.gameRenderer.getBasicProjectionMatrix(((GameRendererAccessor) client.gameRenderer).callGetFov(camera, tickDelta, true)));

		if (shader.getUniform("BasicMat") != null) {
			shader.getUniform("BasicMat").setMat4x4(basicStack.peek().getModel());
		}

		if (shader.getUniform("BobMat") != null) {
			shader.getUniform("BobMat").setMat4x4(matrixStack.peek().getModel());
		}

		if (shader.getUniform("cameraPos") != null) {
			shader.getUniform("cameraPos").setVec3((float) camera.getPos().getX(), (float) camera.getPos().getY(), (float) camera.getPos().getZ());
		}
	}

	@Override
	@ClientOnly
	public MutableQuad modifyQuad(MutableQuad quad) {
		return quad;
	}

}
