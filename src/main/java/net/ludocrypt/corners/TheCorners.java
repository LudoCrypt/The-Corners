package net.ludocrypt.corners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.ludocrypt.corners.client.render.sky.StrongLiminalShader;
import net.ludocrypt.corners.config.CornerConfig;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerEntities;
import net.ludocrypt.corners.init.CornerItems;
import net.ludocrypt.corners.init.CornerPaintings;
import net.ludocrypt.corners.init.CornerSoundEvents;
import net.ludocrypt.corners.init.CornerWorld;
import net.ludocrypt.corners.packet.ClientToServerPackets;
import net.ludocrypt.limlib.impl.LimlibRegistries;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TheCorners implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("The Corners");

	@Override
	public void onInitialize() {
		CornerConfig.init();
		CornerPaintings.init();
		CornerBlocks.init();
		CornerItems.init();
		CornerWorld.init();
		CornerEntities.init();
		CornerSoundEvents.init();
		ClientToServerPackets.manageClientToServerPackets();
		Registry.register(LimlibRegistries.LIMINAL_SHADER_APPLIER, id("stong_simple_shader"), StrongLiminalShader.CODEC);
	}

	public static Identifier id(String id) {
		return new Identifier("corners", id);
	}

}
