package net.ludocrypt.corners.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.ludocrypt.corners.block.entity.SkyboxBlockEntity;
import net.ludocrypt.corners.client.render.block.SkyboxBlockEntityRenderer;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerShaderRegistry;
import net.ludocrypt.corners.init.WorldReverbRegistry;
import net.ludocrypt.corners.mixin.MinecraftClientAccessor;
import net.ludocrypt.corners.packet.ServerToClientPackets;
import net.minecraft.client.MinecraftClient;

public class TheCornersClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		CornerShaderRegistry.init();
		WorldReverbRegistry.init();
		ServerToClientPackets.manageServerToClientPackets();
		BlockEntityRendererRegistry.INSTANCE.register(CornerBlocks.SKYBOX_BLOCK_ENTITY, (context) -> new SkyboxBlockEntityRenderer<SkyboxBlockEntity>());
	}

	public static float getTickDelta() {
		MinecraftClient client = MinecraftClient.getInstance();
		return client.isPaused() ? ((MinecraftClientAccessor) client).getPausedTickDelta() : client.getTickDelta();
	}

}
