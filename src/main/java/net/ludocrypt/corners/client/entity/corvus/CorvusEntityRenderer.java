package net.ludocrypt.corners.client.entity.corvus;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.entity.covrus.CorvusEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class CorvusEntityRenderer extends MobEntityRenderer<CorvusEntity, CorvusEntityModel<CorvusEntity>> {

	public CorvusEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new CorvusEntityModel<>(context.getPart(CorvusEntityModel.LAYER_LOCATION)), 0.5F);
	}

	@Override
	public Identifier getTexture(CorvusEntity entity) {
		return TheCorners.id("textures/entity/corvus/plain.png");
	}

}
