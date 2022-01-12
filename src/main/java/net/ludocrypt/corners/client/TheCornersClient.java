package net.ludocrypt.corners.client;

import java.util.List;

import com.google.common.collect.Lists;

import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.client.render.sky.CommunalCorridorsSky;
import net.ludocrypt.corners.client.render.sky.YearningCanalSky;
import net.ludocrypt.corners.config.CornerConfig;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerShaderRegistry;
import net.ludocrypt.corners.init.CornerWorld;
import net.ludocrypt.corners.mixin.MinecraftClientAccessor;
import net.ludocrypt.corners.packet.ServerToClientPackets;
import net.ludocrypt.limlib.api.render.SkyHook;
import net.ludocrypt.limlib.api.sound.ReverbSettings;
import net.ludocrypt.limlib.impl.render.LiminalDimensionEffects;
import net.ludocrypt.limlib.impl.render.LiminalSkyRendering;
import net.ludocrypt.limlib.impl.sound.LiminalWorldReverb;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class TheCornersClient implements ClientModInitializer {

	public static final List<Identifier> strongShaders = Lists.newArrayList();

	@Override
	public void onInitializeClient() {
		CornerShaderRegistry.init();
		ServerToClientPackets.manageServerToClientPackets();
		ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (CornerConfig.getInstance().disableStrongShaders && strongShaders.contains(client.world.getRegistryKey().getValue())) {
				return;
			}
			CornerShaderRegistry.getCurrent(client).render(tickDelta);
		});
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), CornerBlocks.SNOWY_GLASS_PANE, CornerBlocks.SNOWY_GLASS);

		LiminalSkyRendering.register(CornerWorld.YEARNING_CANAL, new SkyHook.SkyboxSky(TheCorners.id("textures/sky/yearning_canal")));
		LiminalDimensionEffects.register(CornerWorld.YEARNING_CANAL, new YearningCanalSky());
		LiminalWorldReverb.register(CornerWorld.YEARNING_CANAL, new ReverbSettings().setDecayTime(20));

		LiminalSkyRendering.register(CornerWorld.COMMUNAL_CORRIDORS, new SkyHook.SkyboxSky(TheCorners.id("textures/sky/snow")));
		LiminalDimensionEffects.register(CornerWorld.COMMUNAL_CORRIDORS, new CommunalCorridorsSky());
		LiminalWorldReverb.register(CornerWorld.COMMUNAL_CORRIDORS, new ReverbSettings().setDecayTime(2.15F).setDensity(0.725F));
	}

	public static float getTickDelta() {
		MinecraftClient client = MinecraftClient.getInstance();
		return client.isPaused() ? ((MinecraftClientAccessor) client).getPausedTickDelta() : client.getTickDelta();
	}

}
