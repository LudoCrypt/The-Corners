package net.ludocrypt.corners.client;

import java.util.Optional;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.client.render.sky.CommunalCorridorsSky;
import net.ludocrypt.corners.client.render.sky.StrongLiminalShader;
import net.ludocrypt.corners.client.render.sky.YearningCanalSky;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerSoundEvents;
import net.ludocrypt.corners.init.CornerWorld;
import net.ludocrypt.corners.mixin.MinecraftClientAccessor;
import net.ludocrypt.corners.packet.ServerToClientPackets;
import net.ludocrypt.limlib.api.render.LiminalShader;
import net.ludocrypt.limlib.api.render.SkyHook;
import net.ludocrypt.limlib.api.sound.ReverbSettings;
import net.ludocrypt.limlib.impl.render.LiminalDimensionEffects;
import net.ludocrypt.limlib.impl.render.LiminalShaderRegistry;
import net.ludocrypt.limlib.impl.render.LiminalSkyRendering;
import net.ludocrypt.limlib.impl.sound.LiminalWorldMusic;
import net.ludocrypt.limlib.impl.sound.LiminalWorldReverb;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.sound.MusicSound;

public class TheCornersClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ServerToClientPackets.manageServerToClientPackets();
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), CornerBlocks.SNOWY_GLASS_PANE, CornerBlocks.SNOWY_GLASS);

		LiminalSkyRendering.register(CornerWorld.YEARNING_CANAL, new SkyHook.SkyboxSky(TheCorners.id("textures/sky/yearning_canal")));
		LiminalDimensionEffects.register(CornerWorld.YEARNING_CANAL, new YearningCanalSky());
		LiminalWorldReverb.register(CornerWorld.YEARNING_CANAL, new ReverbSettings().setDecayTime(20));
		LiminalWorldMusic.register(CornerWorld.YEARNING_CANAL, Optional.of(new MusicSound(CornerSoundEvents.MUSIC_YEARNING_CANAL, 3000, 8000, true)));
		LiminalShaderRegistry.register(CornerWorld.YEARNING_CANAL, new LiminalShader.SimpleShader(TheCorners.id("yearning_canal")));

		LiminalSkyRendering.register(CornerWorld.COMMUNAL_CORRIDORS, new SkyHook.SkyboxSky(TheCorners.id("textures/sky/snow")));
		LiminalDimensionEffects.register(CornerWorld.COMMUNAL_CORRIDORS, new CommunalCorridorsSky());
		LiminalWorldReverb.register(CornerWorld.COMMUNAL_CORRIDORS, new ReverbSettings().setDecayTime(2.15F).setDensity(0.725F));
		LiminalWorldMusic.register(CornerWorld.COMMUNAL_CORRIDORS, Optional.of(new MusicSound(CornerSoundEvents.MUSIC_COMMUNAL_CORRIDORS, 3000, 8000, true)));
		LiminalShaderRegistry.register(CornerWorld.COMMUNAL_CORRIDORS, new StrongLiminalShader(TheCorners.id("communal_corridors")));
	}

	public static float getTickDelta() {
		MinecraftClient client = MinecraftClient.getInstance();
		return client.isPaused() ? ((MinecraftClientAccessor) client).getPausedTickDelta() : client.getTickDelta();
	}

}
