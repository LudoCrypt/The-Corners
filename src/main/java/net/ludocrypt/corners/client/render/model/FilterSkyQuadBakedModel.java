package net.ludocrypt.corners.client.render.model;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;

import org.lwjgl.system.MemoryStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vector4f;

public class FilterSkyQuadBakedModel implements BakedModel {

	BakedModel wrapper;

	public FilterSkyQuadBakedModel(BakedModel wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
		return wrapper.getQuads(state, face, random).stream().filter((quad) -> !quad.getSprite().getId().getPath().startsWith("sky/")).toList();
	}

	@Override
	public boolean useAmbientOcclusion() {
		return wrapper.useAmbientOcclusion();
	}

	@Override
	public boolean hasDepth() {
		return wrapper.hasDepth();
	}

	@Override
	public boolean isSideLit() {
		return wrapper.isSideLit();
	}

	@Override
	public boolean isBuiltin() {
		return wrapper.isBuiltin();
	}

	@Override
	public Sprite getParticleSprite() {
		return wrapper.getParticleSprite();
	}

	@Override
	public ModelTransformation getTransformation() {
		return wrapper.getTransformation();
	}

	@Override
	public ModelOverrideList getOverrides() {
		return wrapper.getOverrides();
	}

	public static void quad(VertexConsumer consumer, Matrix4f matrix4f, BakedQuad quad) {
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
				consumer.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ()).next();
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
