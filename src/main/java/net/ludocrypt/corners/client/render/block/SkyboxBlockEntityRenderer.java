package net.ludocrypt.corners.client.render.block;

import java.util.function.Function;

import ladysnake.satin.api.managed.ManagedCoreShader;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.ludocrypt.corners.block.entity.SkyboxBlockEntity;
import net.ludocrypt.corners.mixin.RenderLayerAccessor;
import net.ludocrypt.corners.util.CachedFunction;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

public class SkyboxBlockEntityRenderer<T extends SkyboxBlockEntity> implements BlockEntityRenderer<T> {

	public static Shader SKYBOX_SHADER;
	public static final RenderPhase.Shader SKYBOX_RENDER_PHASE = new RenderPhase.Shader(() -> SKYBOX_SHADER);
	public static final Function<Identifier, RenderLayer> SKYBOX_RENDER_LAYER = CachedFunction.memoize((skybox) -> RenderLayerAccessor.callOf("rendertype_corners_skybox", VertexFormats.POSITION, VertexFormat.DrawMode.QUADS, 256, false, false, RenderLayer.MultiPhaseParameters.builder().shader(SKYBOX_RENDER_PHASE).texture(RenderPhase.Textures.create().add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_0.png"), false, false).add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_1.png"), false, false).add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_2.png"), false, false).add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_3.png"), false, false).add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_4.png"), false, false).add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_5.png"), false, false).build()).build(false)));
	public static final RenderLayer SKYBOX_STATIC_RENDER_LAYER = SKYBOX_RENDER_LAYER.apply(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
	public static final ManagedCoreShader SKYBOX_CORE_SHADER = ShaderEffectManager.getInstance().manageCoreShader(new Identifier("rendertype_corners_skybox"), VertexFormats.POSITION);

	@Override
	public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.push();
		Matrix4f matrix = matrices.peek().getModel();
		SKYBOX_CORE_SHADER.findUniformMat4("TransformMatrix").set(matrix);
		renderSides(entity, matrix, vertexConsumers.getBuffer(SKYBOX_CORE_SHADER.getRenderLayer(SKYBOX_RENDER_LAYER.apply(entity.skyboxId))));
		matrices.pop();
	}

	private void renderSides(T entity, Matrix4f matrix, VertexConsumer vertexConsumer) {
		this.renderSide(entity, matrix, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
		this.renderSide(entity, matrix, vertexConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
		this.renderSide(entity, matrix, vertexConsumer, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
		this.renderSide(entity, matrix, vertexConsumer, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
		this.renderSide(entity, matrix, vertexConsumer, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.UP);
		this.renderSide(entity, matrix, vertexConsumer, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, Direction.DOWN);
	}

	private void renderSide(T entity, Matrix4f model, VertexConsumer vertices, float x1, float x2, float y1, float y2, float z1, float z2, float z3, float z4, Direction side) {
		if (Block.shouldDrawSide(entity.getCachedState(), entity.getWorld(), entity.getPos(), side, entity.getPos().offset(side))) {
			vertices.vertex(model, x1, y1, z1).next();
			vertices.vertex(model, x2, y1, z2).next();
			vertices.vertex(model, x2, y2, z3).next();
			vertices.vertex(model, x1, y2, z4).next();
		}
	}

}
