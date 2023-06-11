package net.ludocrypt.corners.client;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;

import net.fabricmc.fabric.impl.client.rendering.EntityRendererRegistryImpl;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerEntities;
import net.ludocrypt.corners.packet.ServerToClientPackets;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.PaintingEntityRenderer;

public class TheCornersClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		ServerToClientPackets.manageServerToClientPackets();
		BlockRenderLayerMap.put(RenderLayer.getCutout(), CornerBlocks.SNOWY_GLASS_PANE, CornerBlocks.SNOWY_GLASS, CornerBlocks.SNOWY_GLASS_SLAB);
		EntityRendererRegistryImpl.register(CornerEntities.DIMENSIONAL_PAINTING_ENTITY, PaintingEntityRenderer::new);
	}

}
