package net.ludocrypt.corners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import eu.midnightdust.lib.config.MidnightConfig;
import net.ludocrypt.corners.client.render.StrongPostEffect;
import net.ludocrypt.corners.config.CornerConfig;
import net.ludocrypt.corners.init.CornerBiomes;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerEntities;
import net.ludocrypt.corners.init.CornerModelRenderers;
import net.ludocrypt.corners.init.CornerPaintings;
import net.ludocrypt.corners.init.CornerRadioRegistry;
import net.ludocrypt.corners.init.CornerSoundEvents;
import net.ludocrypt.corners.packet.ClientToServerPackets;
import net.ludocrypt.limlib.effects.post.PostEffect;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TheCorners implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("The Corners");

	@Override
	public void onInitialize(ModContainer mod) {
		MidnightConfig.init("the_corners", CornerConfig.class);
		CornerPaintings.init();
		CornerBlocks.init();
		CornerBiomes.init();
		CornerEntities.init();
		CornerSoundEvents.init();
		CornerRadioRegistry.init();
		CornerModelRenderers.init();
		ClientToServerPackets.manageClientToServerPackets();
		Registry.register(PostEffect.POST_EFFECT_CODEC, id("strong_shader"), StrongPostEffect.CODEC);
	}

	public static Identifier id(String id) {
		return new Identifier("corners", id);
	}

}
