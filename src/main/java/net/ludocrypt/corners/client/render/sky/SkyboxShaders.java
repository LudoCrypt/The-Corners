package net.ludocrypt.corners.client.render.sky;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import org.lwjgl.system.MemoryStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

public class SkyboxShaders {

	public static Shader SKYBOX_SHADER;
	public static final RenderPhase.Shader SKYBOX_RENDER_PHASE = new RenderPhase.Shader(() -> SKYBOX_SHADER);

	public static void addAll(List<BakedQuad> list, BakedModel model, BlockState state, Direction dir, Random random) {
		list.addAll(model.getQuads(state, dir, random).stream().filter((quad) -> quad.getSprite().getId().getPath().startsWith("sky/")).toList());
	}

	public static void addAll(List<BakedQuad> list, BakedModel model, BlockState state, Direction dir) {
		addAll(list, model, state, dir, new Random(0));
	}

	public static void addAll(List<BakedQuad> list, BakedModel model, BlockState state) {
		addAll(list, model, state, (Direction) null);
	}

	public static void addAll(List<BakedQuad> list, BakedModel model, BlockState state, Random random) {
		addAll(list, model, state, null, random);
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
