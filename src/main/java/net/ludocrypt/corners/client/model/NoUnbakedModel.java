package net.ludocrypt.corners.client.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

public class NoUnbakedModel extends JsonUnbakedModel {

	public NoUnbakedModel() {
		super(null, List.of(), Maps.newHashMap(), false, null, ModelTransformation.NONE, List.of());
	}

	@Override
	public Collection<Identifier> getModelDependencies() {
		return Lists.newArrayList();
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
		return Lists.newArrayList();
	}

	@Override
	public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
		return new NoBakedModel();
	}

}
