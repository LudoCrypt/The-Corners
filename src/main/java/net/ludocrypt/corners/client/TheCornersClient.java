package net.ludocrypt.corners.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.mixin.MinecraftClientAccessor;
import net.ludocrypt.corners.packet.ServerToClientPackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;

public class TheCornersClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ServerToClientPackets.manageServerToClientPackets();
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), CornerBlocks.SNOWY_GLASS_PANE, CornerBlocks.SNOWY_GLASS);
	}

	public static float getTickDelta() {
		MinecraftClient client = MinecraftClient.getInstance();
		return client.isPaused() ? ((MinecraftClientAccessor) client).getPausedTickDelta() : client.getTickDelta();
	}

}
