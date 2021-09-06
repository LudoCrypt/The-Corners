package net.ludocrypt.corners.init;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.ludocrypt.corners.TheCorners;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;

public class CornerRadioRegistry {

	public static final SimpleRegistry<SoundEvent> RADIO_REGISTRY = FabricRegistryBuilder.createDefaulted(SoundEvent.class, TheCorners.id("radio_registry"), TheCorners.id("default_radio")).attribute(RegistryAttribute.SYNCED).buildAndRegister();
	public static final SoundEvent DEFAULT = CornerSoundEvents.RADIO_DEFAULT_STATIC;

	public static void init() {
		Registry.register(RADIO_REGISTRY, TheCorners.id("default_radio"), DEFAULT);
	}

	public static SoundEvent register(RegistryKey<World> world, SoundEvent sound) {
		return Registry.register(RADIO_REGISTRY, world.getValue(), sound);
	}

	public static SoundEvent getCurrent(MinecraftClient client) {
		return getCurrent(client.world.getRegistryKey());
	}

	public static SoundEvent getCurrent(RegistryKey<World> key) {
		return RADIO_REGISTRY.getOrEmpty(key.getValue()).orElse(DEFAULT);
	}

}
