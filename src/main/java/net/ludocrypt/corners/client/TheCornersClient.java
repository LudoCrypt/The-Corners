package net.ludocrypt.corners.client;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.mutable.Mutable;

import com.google.common.collect.Lists;

import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.config.CornerConfig;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerShaderRegistry;
import net.ludocrypt.corners.init.CornerSoundEvents;
import net.ludocrypt.corners.init.CornerWorld;
import net.ludocrypt.corners.mixin.MinecraftClientAccessor;
import net.ludocrypt.corners.packet.ServerToClientPackets;
import net.ludocrypt.limlib.api.render.SkyHook;
import net.ludocrypt.limlib.api.sound.LiminalTravelSound;
import net.ludocrypt.limlib.api.sound.ReverbSettings;
import net.ludocrypt.limlib.impl.render.LiminalSkyRendering;
import net.ludocrypt.limlib.impl.sound.LiminalTravelSounds;
import net.ludocrypt.limlib.impl.sound.LiminalWorldReverb;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class TheCornersClient implements ClientModInitializer {

	public static final List<Identifier> strongShaders = Lists.newArrayList();
	public static boolean comingFromPainting = false;

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
		LiminalSkyRendering.register(CornerWorld.YEARNING_CANAL.worldWorldRegistryKey, new SkyHook.SkyboxSky(TheCorners.id("textures/sky/yearning_canal")));
		LiminalTravelSounds.register(CornerWorld.YEARNING_CANAL.worldId, new LiminalTravelSound.SimpleTravelSound(CornerWorld.YEARNING_CANAL.worldWorldRegistryKey, CornerSoundEvents.PAINTING_PORTAL_TRAVEL));
		LiminalWorldReverb.register(CornerWorld.YEARNING_CANAL.worldWorldRegistryKey, new ReverbSettings().setDecayTime(20));

		LiminalSkyRendering.register(CornerWorld.COMMUNAL_CORRIDORS.worldWorldRegistryKey, new SkyHook.SkyboxSky(TheCorners.id("textures/sky/snow")));
		LiminalTravelSounds.register(CornerWorld.COMMUNAL_CORRIDORS.worldId, new LiminalTravelSound.SimpleTravelSound(CornerWorld.COMMUNAL_CORRIDORS.worldWorldRegistryKey, CornerSoundEvents.PAINTING_PORTAL_TRAVEL));
		LiminalWorldReverb.register(CornerWorld.COMMUNAL_CORRIDORS.worldWorldRegistryKey, new ReverbSettings().setDecayTime(2.15F).setDensity(0.725F));

		LiminalTravelSounds.register(TheCorners.id("from_corners"), new LiminalTravelSound() {
			@Override
			public void hookSound(ServerWorld from, ServerWorld to, Mutable<Optional<SoundEvent>> mutable) {
				if (comingFromPainting) {
					mutable.setValue(Optional.of(CornerSoundEvents.PAINTING_PORTAL_TRAVEL));
				}

				comingFromPainting = false;
			}

			@Override
			public int priority() {
				return 1500;
			}
		});
	}

	public static float getTickDelta() {
		MinecraftClient client = MinecraftClient.getInstance();
		return client.isPaused() ? ((MinecraftClientAccessor) client).getPausedTickDelta() : client.getTickDelta();
	}

}
