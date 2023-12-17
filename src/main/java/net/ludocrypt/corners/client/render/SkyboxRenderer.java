package net.ludocrypt.corners.client.render;

import org.joml.Matrix4f;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import com.mojang.blaze3d.systems.RenderSystem;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.mixin.GameRendererAccessor;
import net.ludocrypt.specialmodels.api.SpecialModelRenderer;
import net.ludocrypt.specialmodels.impl.render.MutableQuad;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.ShaderProgram;
import net.minecraft.client.render.chunk.ChunkRenderRegion;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;

public class SkyboxRenderer extends SpecialModelRenderer {

	private final String id;

	public SkyboxRenderer(String id) {
		this.id = id;
	}

	@Override
	@ClientOnly
	public void setup(MatrixStack matrices, Matrix4f viewMatrix, Matrix4f positionMatrix, float tickDelta,
			ShaderProgram shader, BlockPos origin) {

		for (int i = 0; i < 6; i++) {
			RenderSystem.setShaderTexture(i, TheCorners.id("textures/sky/" + id + "_" + i + ".png"));
		}

		MinecraftClient client = MinecraftClient.getInstance();
		Camera camera = client.gameRenderer.getCamera();
		Matrix4f matrix = new MatrixStack().peek().getModel();
		matrix.rotate(Axis.X_POSITIVE.rotationDegrees(camera.getPitch()));
		matrix.rotate(Axis.Y_POSITIVE.rotationDegrees(camera.getYaw() + 180.0F));

		if (shader.getUniform("RotMat") != null) {
			shader.getUniform("RotMat").setMat4x4(matrix);
		}

		MatrixStack matrixStack = new MatrixStack();
		((GameRendererAccessor) client.gameRenderer).callBobViewWhenHurt(matrixStack, tickDelta);

		if (client.options.getBobView().get()) {
			((GameRendererAccessor) client.gameRenderer).callBobView(matrixStack, tickDelta);
		}

		if (shader.getUniform("bobMat") != null) {
			shader.getUniform("bobMat").setMat4x4(matrixStack.peek().getModel());
		}

	}

	@Override
	@ClientOnly
	public MutableQuad modifyQuad(ChunkRenderRegion chunkRenderRegion, BlockPos pos, BlockState state, BakedModel model,
			BakedQuad quadIn, long modelSeed, MutableQuad quad) {
		quad.getV1().setUv(new Vec2f(0.0F, 0.0F));
		quad.getV2().setUv(new Vec2f(0.0F, 1.0F));
		quad.getV3().setUv(new Vec2f(1.0F, 1.0F));
		quad.getV4().setUv(new Vec2f(1.0F, 0.0F));
		return quad;
	}

}
