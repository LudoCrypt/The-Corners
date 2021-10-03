package net.ludocrypt.corners.client;

import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.ludocrypt.corners.block.entity.SkyboxBlockEntity;
import net.ludocrypt.corners.client.render.block.SkyboxBlockEntityRenderer;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerShaderRegistry;
import net.ludocrypt.corners.init.WorldReverbRegistry;
import net.ludocrypt.corners.mixin.MinecraftClientAccessor;
import net.ludocrypt.corners.packet.ServerToClientPackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;

public class TheCornersClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		CornerShaderRegistry.init();
		WorldReverbRegistry.init();
		ServerToClientPackets.manageServerToClientPackets();
		BlockEntityRendererRegistry.INSTANCE.register(CornerBlocks.SKYBOX_BLOCK_ENTITY, (context) -> new SkyboxBlockEntityRenderer<SkyboxBlockEntity>());
		ShaderEffectRenderCallback.EVENT.register(tickDelta -> CornerShaderRegistry.getCurrent(MinecraftClient.getInstance().world.getRegistryKey()).render(tickDelta));
		BlockRenderLayerMap.INSTANCE.putBlock(CornerBlocks.SNOWY_GLASS_PANE, RenderLayer.getTranslucent());
	}

	public static float getTickDelta() {
		MinecraftClient client = MinecraftClient.getInstance();
		return client.isPaused() ? ((MinecraftClientAccessor) client).getPausedTickDelta() : client.getTickDelta();
	}

}
