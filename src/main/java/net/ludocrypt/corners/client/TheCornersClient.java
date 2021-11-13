package net.ludocrypt.corners.client;

import java.util.List;

import com.google.common.collect.Lists;

import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.ludocrypt.corners.config.CornerConfig;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerShaderRegistry;
import net.ludocrypt.corners.init.WorldReverbRegistry;
import net.ludocrypt.corners.mixin.MinecraftClientAccessor;
import net.ludocrypt.corners.packet.ServerToClientPackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class TheCornersClient implements ClientModInitializer {

	public static final List<Identifier> strongShaders = Lists.newArrayList();

	@Override
	public void onInitializeClient() {
		CornerShaderRegistry.init();
		WorldReverbRegistry.init();
		ServerToClientPackets.manageServerToClientPackets();
		ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (CornerConfig.getInstance().disableStrongShaders && strongShaders.contains(client.world.getRegistryKey().getValue())) {
				return;
			}
			CornerShaderRegistry.getCurrent(client).render(tickDelta);
		});
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), CornerBlocks.SNOWY_GLASS_PANE, CornerBlocks.SNOWY_GLASS);
	}

	public static float getTickDelta() {
		MinecraftClient client = MinecraftClient.getInstance();
		return client.isPaused() ? ((MinecraftClientAccessor) client).getPausedTickDelta() : client.getTickDelta();
	}

}
