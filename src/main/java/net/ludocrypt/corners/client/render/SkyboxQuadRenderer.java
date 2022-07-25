package net.ludocrypt.corners.client.render;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;

import net.ludocrypt.limlib.api.render.LiminalQuadRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class SkyboxQuadRenderer extends LiminalQuadRenderer {

	@Override
	public void renderQuad(BakedQuad quad, BufferBuilder bufferBuilder, Matrix4f matrix, Camera camera, World world, MatrixStack matrices, BakedModel model, @Nullable BlockState state, @Nullable Direction dir, Random random) {
		RenderSystem.setShader(CornerShaders.SKYBOX_SHADER::getShader);

		for (int i = 0; i < 6; i++) {
			RenderSystem.setShaderTexture(i, new Identifier(quad.getSprite().getId().getNamespace(), "textures/" + quad.getSprite().getId().getPath() + "_" + i + ".png"));
		}

		LiminalQuadRenderer.quad((vec3f) -> bufferBuilder.vertex(vec3f.getX(), vec3f.getY(), vec3f.getZ()).next(), matrix, quad);
	}

	@Override
	public boolean renderBehind() {
		return true;
	}

	@Override
	public VertexFormat vertexFormat() {
		return VertexFormats.POSITION;
	}

	@Override
	public DrawMode drawMode() {
		return DrawMode.QUADS;
	}

}
