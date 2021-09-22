package net.ludocrypt.corners.client.render.block;

import ladysnake.satin.api.managed.ManagedCoreShader;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.block.entity.SkyboxBlockEntity;
import net.ludocrypt.corners.mixin.RenderLayerAccessor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

public class SkyboxBlockEntityRenderer<T extends SkyboxBlockEntity> implements BlockEntityRenderer<T> {

	public static Shader skyboxShader;
	public static final RenderPhase.Shader SKYBOX_SHADER = new RenderPhase.Shader(() -> skyboxShader);
	public static final RenderLayer PELLUCID_PALACE_RENDER_LAYER = ofSkybox("pellucid_palace", TheCorners.id("textures/sky/pellucid_palace"));
	public static final ManagedCoreShader SKYBOX_CORE_SHADER = ShaderEffectManager.getInstance().manageCoreShader(new Identifier("rendertype_corners_skybox"), VertexFormats.POSITION_TEXTURE);

	@Override
	public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		Matrix4f matrix = matrices.peek().getModel();
		Matrix4f invert = matrix.copy();
		invert.invert();
		SKYBOX_CORE_SHADER.findUniformMat4("InverseTransformMatrix").set(invert);
		renderSides(entity, matrix, vertexConsumers.getBuffer(SKYBOX_CORE_SHADER.getRenderLayer(PELLUCID_PALACE_RENDER_LAYER)));
	}

	private void renderSides(T entity, Matrix4f matrix, VertexConsumer vertexConsumer) {
		this.renderSide(entity, matrix, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.SOUTH);
		this.renderSide(entity, matrix, vertexConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.NORTH);
		this.renderSide(entity, matrix, vertexConsumer, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
		this.renderSide(entity, matrix, vertexConsumer, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
		this.renderSide(entity, matrix, vertexConsumer, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
		this.renderSide(entity, matrix, vertexConsumer, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
	}

	private void renderSide(T entity, Matrix4f model, VertexConsumer vertices, float x1, float x2, float y1, float y2, float z1, float z2, float z3, float z4, Direction side) {
		vertices.vertex(model, x1, y1, z1).next();
		vertices.vertex(model, x2, y1, z2).next();
		vertices.vertex(model, x2, y2, z3).next();
		vertices.vertex(model, x1, y2, z4).next();
	}

	public static RenderLayer ofSkybox(String name, Identifier skybox) {
		return RenderLayerAccessor.callOf("rendertype_corners_skybox_" + name, VertexFormats.POSITION, VertexFormat.DrawMode.QUADS, 256, false, false, RenderLayer.MultiPhaseParameters.builder().shader(SKYBOX_SHADER).texture(RenderPhase.Textures.create().add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_0.png"), false, false).add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_1.png"), false, false).add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_2.png"), false, false).add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_3.png"), false, false).add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_4.png"), false, false).add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_5.png"), false, false).build()).build(false));
	}

}
