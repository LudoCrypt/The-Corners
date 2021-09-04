package net.ludocrypt.corners.client;

import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import net.fabricmc.api.ClientModInitializer;
import net.ludocrypt.corners.init.CornerShaderRegistry;
import net.ludocrypt.corners.packet.CornersPacketManager;
import net.minecraft.client.MinecraftClient;

public class TheCornersClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		CornersPacketManager.manageClientToServerPackets();
		ShaderEffectRenderCallback.EVENT.register(tickDelta -> CornerShaderRegistry.getCurrent(MinecraftClient.getInstance().world.getRegistryKey()).render(tickDelta));
	}

}
