package net.ludocrypt.corners.client.render;

import org.joml.Matrix4f;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import com.mojang.blaze3d.systems.RenderSystem;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.mixin.GameRendererAccessor;
import net.ludocrypt.specialmodels.api.SpecialModelRenderer;
import net.ludocrypt.specialmodels.impl.render.MutableQuad;
import net.ludocrypt.specialmodels.impl.render.Vec4b;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.ShaderProgram;
import net.minecraft.client.render.chunk.ChunkRenderRegion;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class DeepBookshelfRenderer extends SpecialModelRenderer {

	public static final Identifier DEEP_BOOKSHELF_ATLAS_TEXTURE = TheCorners.id("textures/atlas/deep.png");

	@Override
	@ClientOnly
	public void setup(MatrixStack matrices, Matrix4f viewMatrix, Matrix4f positionMatrix, float tickDelta,
			ShaderProgram shader, BlockPos origin) {
		RenderSystem.enablePolygonOffset();
		RenderSystem.polygonOffset(-3.0F, -3.0F);
		RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
		RenderSystem.setShaderTexture(1, DEEP_BOOKSHELF_ATLAS_TEXTURE);
		MinecraftClient client = MinecraftClient.getInstance();
		Camera camera = client.gameRenderer.getCamera();
		MatrixStack matrixStack = new MatrixStack();
		((GameRendererAccessor) client.gameRenderer).callBobViewWhenHurt(matrixStack, tickDelta);

		if (client.options.getBobView().get()) {
			((GameRendererAccessor) client.gameRenderer).callBobView(matrixStack, tickDelta);
		}

		MatrixStack basicStack = new MatrixStack();
		basicStack
			.multiplyMatrix(client.gameRenderer
				.getBasicProjectionMatrix(((GameRendererAccessor) client.gameRenderer).callGetFov(camera, tickDelta, true)));

		if (shader.getUniform("BasicMat") != null) {
			shader.getUniform("BasicMat").setMat4x4(basicStack.peek().getModel());
		}

		if (shader.getUniform("BobMat") != null) {
			shader.getUniform("BobMat").setMat4x4(matrixStack.peek().getModel());
		}

		if (shader.getUniform("cameraPos") != null) {
			shader
				.getUniform("cameraPos")
				.setVec3((float) camera.getPos().getX(), (float) camera.getPos().getY(), (float) camera.getPos().getZ());
		}

	}

	@Override
	@ClientOnly
	public MutableQuad modifyQuad(MutableQuad quad) {
		return quad;
	}

	@Override
	@ClientOnly
	public Vec4b appendState(ChunkRenderRegion chunkRenderRegion, BlockPos pos, BlockState state, long modelSeed) {

		if (state.isOf(CornerBlocks.DEEP_BOOKSHELF)) {
			byte b1 = 0;
			byte b2 = 0;
			byte b3 = 0;
			byte b4 = 0;

			if (state.get(Properties.SLOT_0_OCCUPIED)) {
				b1 += 1;
			}

			if (state.get(Properties.SLOT_1_OCCUPIED)) {
				b1 += 2;
			}

			if (state.get(Properties.SLOT_2_OCCUPIED)) {
				b1 += 4;
			}

			if (state.get(Properties.SLOT_3_OCCUPIED)) {
				b2 += 1;
			}

			if (state.get(Properties.SLOT_4_OCCUPIED)) {
				b2 += 2;
			}

			if (state.get(Properties.SLOT_5_OCCUPIED)) {
				b2 += 4;
			}

			return new Vec4b(b1, b2, b3, b4);
		}

		return super.appendState(chunkRenderRegion, pos, state, modelSeed);
	}

}
