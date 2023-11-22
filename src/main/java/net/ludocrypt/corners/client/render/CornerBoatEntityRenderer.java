package net.ludocrypt.corners.client.render;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import net.ludocrypt.corners.entity.CornerBoatEntity;
import net.minecraft.client.render.entity.BoatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.ChestBoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public final class CornerBoatEntityRenderer extends BoatEntityRenderer {

	public CornerBoatEntityRenderer(EntityRendererFactory.Context context, boolean chest,
			CornerBoatEntity.CornerBoat boatData) {
		super(context, chest);
		var id = boatData.id();
		var texture = new Identifier(id.getNamespace(),
			"textures/entity/" + (chest ? "chest_boat/" : "boat/") + id.getPath() + ".png");
		var rootPart = context.getPart(getModelLayer(boatData, chest));
		var model = chest ? new ChestBoatEntityModel(rootPart) : new BoatEntityModel(rootPart);
		this.texturesAndModels = this.texturesAndModels
			.entrySet()
			.stream()
			.collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> Pair.of(texture, model)));
	}

	public static EntityModelLayer getModelLayer(CornerBoatEntity.CornerBoat boat, boolean chest) {
		var id = boat.id();
		return new EntityModelLayer(new Identifier(id.getNamespace(), (chest ? "chest_boat/" : "boat/") + id.getPath()),
			"main");
	}

}
