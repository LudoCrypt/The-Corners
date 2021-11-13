package net.ludocrypt.corners.client.render.sky;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import org.lwjgl.system.MemoryStack;

import ladysnake.satin.api.managed.ManagedCoreShader;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.ludocrypt.corners.mixin.RenderLayerAccessor;
import net.ludocrypt.corners.util.CachedFunction;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

public class SkyboxShaders {

	public static Shader SKYBOX_SHADER;
	public static final RenderPhase.Shader SKYBOX_RENDER_PHASE = new RenderPhase.Shader(() -> SKYBOX_SHADER);
	public static final Function<Identifier, RenderLayer> SKYBOX_RENDER_LAYER = CachedFunction.memoize((skybox) -> RenderLayerAccessor.callOf("rendertype_corners_skybox", VertexFormats.POSITION, VertexFormat.DrawMode.QUADS, 256, false, false, RenderLayer.MultiPhaseParameters.builder().shader(SKYBOX_RENDER_PHASE).texture(RenderPhase.Textures.create().add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_0.png"), false, false).add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_1.png"), false, false).add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_2.png"), false, false).add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_3.png"), false, false).add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_4.png"), false, false).add(new Identifier(skybox.getNamespace(), skybox.getPath() + "_5.png"), false, false).build()).build(false)));
	public static final ManagedCoreShader SKYBOX_CORE_SHADER = ShaderEffectManager.getInstance().manageCoreShader(new Identifier("rendertype_corners_skybox"), VertexFormats.POSITION);

	public static void addAll(List<BakedQuad> list, BakedModel model, BlockState state, Direction dir) {
		list.addAll(model.getQuads(state, dir, new Random(0)).stream().filter((quad) -> quad.getSprite().getId().getPath().startsWith("sky/")).toList());
	}

	public static void addAll(List<BakedQuad> list, BakedModel model, BlockState state) {
		addAll(list, model, state, null);
	}

	public static void quad(Consumer<Vec3f> consumer, Matrix4f matrix4f, BakedQuad quad) {
		int[] js = quad.getVertexData();
		int j = js.length / 8;
		MemoryStack memoryStack = MemoryStack.stackPush();
		try {
			ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSize());
			IntBuffer intBuffer = byteBuffer.asIntBuffer();

			for (int k = 0; k < j; ++k) {
				intBuffer.clear();
				intBuffer.put(js, k * 8, 8);
				float f = byteBuffer.getFloat(0);
				float g = byteBuffer.getFloat(4);
				float h = byteBuffer.getFloat(8);

				Vector4f vector4f = new Vector4f(f, g, h, 1.0F);
				vector4f.transform(matrix4f);
				consumer.accept(new Vec3f(vector4f.getX(), vector4f.getY(), vector4f.getZ()));
			}
		} catch (Throwable var33) {
			if (memoryStack != null) {
				try {
					memoryStack.close();
				} catch (Throwable var32) {
					var33.addSuppressed(var32);
				}
			}

			throw var33;
		}
		if (memoryStack != null) {
			memoryStack.close();
		}
	}

}
