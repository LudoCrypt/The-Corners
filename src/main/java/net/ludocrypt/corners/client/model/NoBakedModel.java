package net.ludocrypt.corners.client.model;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.Direction;

public class NoBakedModel implements BakedModel {

	public NoBakedModel() {
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
		return Lists.newArrayList();
	}

	@Override
	public boolean useAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean hasDepth() {
		return false;
	}

	@Override
	public boolean isSideLit() {
		return false;
	}

	@Override
	public boolean isBuiltin() {
		return false;
	}

	@Override
	public Sprite getParticleSprite() {
		return MissingSprite.getMissingSprite(new SpriteAtlasTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE), 0, 16, 16, 0, 0);
	}

	@Override
	public ModelTransformation getTransformation() {
		return ModelTransformation.NONE;
	}

	@Override
	public ModelOverrideList getOverrides() {
		return ModelOverrideList.EMPTY;
	}

}
