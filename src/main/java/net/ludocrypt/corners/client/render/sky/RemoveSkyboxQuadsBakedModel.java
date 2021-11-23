package net.ludocrypt.corners.client.render.sky;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class RemoveSkyboxQuadsBakedModel implements BakedModel {

	BakedModel wrapper;

	public RemoveSkyboxQuadsBakedModel(BakedModel wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
		List<BakedQuad> quads = wrapper.getQuads(state, face, random);
		if (quads == null) {
			quads = Lists.newArrayList();
		}
		return quads.stream().filter((quad) -> !quad.getSprite().getId().getPath().startsWith("sky/")).toList();
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

}
