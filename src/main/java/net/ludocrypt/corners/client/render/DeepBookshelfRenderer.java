package net.ludocrypt.corners.client.render;

import java.lang.reflect.Field;
import java.text.NumberFormat;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import com.mojang.blaze3d.systems.RenderSystem;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.mixin.FrustumAccessor;
import net.ludocrypt.corners.mixin.GameRendererAccessor;
import net.ludocrypt.corners.mixin.WorldRendererAccessor;
import net.ludocrypt.specialmodels.api.SpecialModelRenderer;
import net.ludocrypt.specialmodels.impl.render.MutableQuad;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.ShaderProgram;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec2f;

public class DeepBookshelfRenderer extends SpecialModelRenderer {

	@Override
	@ClientOnly
	public void setup(MatrixStack matrices, Matrix4f viewMatrix, Matrix4f positionMatrix, float tickDelta, ShaderProgram shader) {
		RenderSystem.setShaderTexture(0, TheCorners.id("textures/block/broken_radio_front.png"));

		MinecraftClient client = MinecraftClient.getInstance();
		Camera camera = client.gameRenderer.getCamera();

		MatrixStack matrixStack = new MatrixStack();

		((GameRendererAccessor) client.gameRenderer).callBobViewWhenHurt(matrixStack, tickDelta);
		if (client.options.getBobView().get()) {
			((GameRendererAccessor) client.gameRenderer).callBobView(matrixStack, tickDelta);
		}

		if (shader.getUniform("BobMat") != null) {
			shader.getUniform("BobMat").setMat4x4(matrixStack.peek().getModel());
		}

		if (shader.getUniform("cameraPos") != null) {
			shader.getUniform("cameraPos").setVec3((float) camera.getPos().getX(), (float) camera.getPos().getY(), (float) camera.getPos().getZ());
		}

//		Frustum frustum = ((WorldRendererAccessor) client.worldRenderer).getFrustum();
//		FrustumIntersection intersection = ((FrustumAccessor) frustum).getIntersection();
//		Vector4f[] planes;
//		try {
//			Field planesField = FrustumIntersection.class.getDeclaredField("planes");
//			planesField.setAccessible(true);
//			planes = (Vector4f[]) planesField.get(intersection);
//			if (shader.getUniform("leftPlane") != null) {
//				shader.getUniform("leftPlane").setVec4(planes[0]);
//			}
//			if (shader.getUniform("rightPlane") != null) {
//				shader.getUniform("rightPlane").setVec4(planes[1]);
//			}
//			if (shader.getUniform("bottomPlane") != null) {
//				shader.getUniform("bottomPlane").setVec4(planes[2]);
//			}
//			if (shader.getUniform("topPlane") != null) {
//				shader.getUniform("topPlane").setVec4(planes[3]);
//			}
//			if (shader.getUniform("nearPlane") != null) {
//				shader.getUniform("nearPlane").setVec4(planes[4]);
//			}
//			if (shader.getUniform("farPlane") != null) {
//				shader.getUniform("farPlane").setVec4(planes[5]);
//			}
//		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
//			e.printStackTrace();
//		}

	}

	@Override
	@ClientOnly
	public MutableQuad modifyQuad(MutableQuad quad) {

		quad.getV1().setUv(new Vec2f(0.0F, 0.0F));
		quad.getV2().setUv(new Vec2f(0.0F, 1.0F));
		quad.getV3().setUv(new Vec2f(1.0F, 1.0F));
		quad.getV4().setUv(new Vec2f(1.0F, 0.0F));

		return quad;
	}

}
