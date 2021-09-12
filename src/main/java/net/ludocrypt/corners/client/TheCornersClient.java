package net.ludocrypt.corners.client;

import net.fabricmc.api.ClientModInitializer;
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
	}

	public static float getTickDelta() {
		MinecraftClient client = MinecraftClient.getInstance();
		return client.isPaused() ? ((MinecraftClientAccessor) client).getPausedTickDelta() : client.getTickDelta();
	}

}
