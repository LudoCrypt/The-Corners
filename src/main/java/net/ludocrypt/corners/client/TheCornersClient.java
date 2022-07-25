package net.ludocrypt.corners.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.impl.client.rendering.EntityRendererRegistryImpl;
import net.ludocrypt.corners.client.render.CornerShaders;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerEntities;
import net.ludocrypt.corners.mixin.MinecraftClientAccessor;
import net.ludocrypt.corners.packet.ServerToClientPackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.PaintingEntityRenderer;

public class TheCornersClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ServerToClientPackets.manageServerToClientPackets();
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), CornerBlocks.SNOWY_GLASS_PANE, CornerBlocks.SNOWY_GLASS);
		CornerShaders.init();
		EntityRendererRegistryImpl.register(CornerEntities.DIMENSIONAL_PAINTING_ENTITY, PaintingEntityRenderer::new);
	}

	public static float getTickDelta() {
		MinecraftClient client = MinecraftClient.getInstance();
		return client.isPaused() ? ((MinecraftClientAccessor) client).getPausedTickDelta() : client.getTickDelta();
	}

}
