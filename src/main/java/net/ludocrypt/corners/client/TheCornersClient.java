package net.ludocrypt.corners.client;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.impl.client.rendering.EntityRendererRegistryImpl;
import net.ludocrypt.corners.client.render.CornerBoatEntityRenderer;
import net.ludocrypt.corners.entity.CornerBoatEntity.CornerBoat;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerEntities;
import net.ludocrypt.corners.packet.ServerToClientPackets;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.ChestBoatEntityModel;

public class TheCornersClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		ServerToClientPackets.manageServerToClientPackets();
		BlockRenderLayerMap
			.put(RenderLayer.getCutout(), CornerBlocks.SNOWY_GLASS_PANE, CornerBlocks.SNOWY_GLASS,
				CornerBlocks.SNOWY_GLASS_SLAB, CornerBlocks.GAIA_DOOR, CornerBlocks.GAIA_TRAPDOOR, CornerBlocks.GAIA_SAPLING,
				CornerBlocks.POTTED_GAIA_SAPLING);
		EntityRendererRegistryImpl.register(CornerEntities.DIMENSIONAL_PAINTING_ENTITY, PaintingEntityRenderer::new);
//		EntityRendererRegistryImpl.register(CornerEntities.CORVUS_ENTITY, CorvusEntityRenderer::new);
//		EntityModelLayerRegistry.registerModelLayer(CorvusEntityModel.LAYER_LOCATION, () -> CorvusEntityModel.createBodyLayer());
		EntityRendererRegistry
			.register(CornerBoat.GAIA.entityType(false),
				context -> new CornerBoatEntityRenderer(context, false, CornerBoat.GAIA));
		EntityModelLayerRegistry
			.registerModelLayer(CornerBoatEntityRenderer.getModelLayer(CornerBoat.GAIA, false),
				() -> BoatEntityModel.getTexturedModelData());
		EntityRendererRegistry
			.register(CornerBoat.GAIA.entityType(true),
				context -> new CornerBoatEntityRenderer(context, true, CornerBoat.GAIA));
		EntityModelLayerRegistry
			.registerModelLayer(CornerBoatEntityRenderer.getModelLayer(CornerBoat.GAIA, true),
				() -> ChestBoatEntityModel.getTexturedModelData());
	}

}
