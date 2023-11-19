package net.ludocrypt.corners.init;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.util.RadioSoundTable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.world.World;

public class CornerRadioRegistry {

	public static final RegistryKey<Registry<RadioSoundTable>> RADIO_REGISTRY_KEY = RegistryKey.ofRegistry(TheCorners.id("radio_registry"));
	public static final SimpleRegistry<RadioSoundTable> RADIO_REGISTRY = FabricRegistryBuilder.createDefaulted(RADIO_REGISTRY_KEY, TheCorners.id("default_radio")).attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();
	public static final RadioSoundTable DEFAULT = new RadioSoundTable(CornerSoundEvents.RADIO_DEFAULT_STATIC, CornerSoundEvents.RADIO_DEFAULT_STATIC, CornerSoundEvents.RADIO_DEFAULT_STATIC);

	public static void init() {
		Registry.register(RADIO_REGISTRY, TheCorners.id("default_radio"), DEFAULT);
		getRadio("yearning_canal", new RadioSoundTable(CornerSoundEvents.RADIO_YEARNING_CANAL_MUSIC, CornerSoundEvents.RADIO_YEARNING_CANAL_STATIC, CornerSoundEvents.RADIO_YEARNING_CANAL));
		getRadio("communal_corridors",
				new RadioSoundTable(CornerSoundEvents.RADIO_COMMUNAL_CORRIDORS_MUSIC, CornerSoundEvents.RADIO_COMMUNAL_CORRIDORS_STATIC, CornerSoundEvents.RADIO_COMMUNAL_CORRIDORS));
		getRadio("hoary_crossroads", new RadioSoundTable(CornerSoundEvents.RADIO_HOARY_CROSSROADS_MUSIC, CornerSoundEvents.RADIO_HOARY_CROSSROADS_STATIC, CornerSoundEvents.RADIO_HOARY_CROSSROADS));
	}

	public static RadioSoundTable register(RegistryKey<World> world, RadioSoundTable sound) {
		return Registry.register(RADIO_REGISTRY, world.getValue(), sound);
	}

	public static RadioSoundTable getCurrent(MinecraftClient client) {
		return getCurrent(client.world.getRegistryKey());
	}

	public static RadioSoundTable getCurrent(RegistryKey<World> key) {
		return RADIO_REGISTRY.getOrEmpty(key.getValue()).orElse(DEFAULT);
	}

	public static <T extends RadioSoundTable> T getRadio(String id, T radio) {
		return Registry.register(CornerRadioRegistry.RADIO_REGISTRY, TheCorners.id(id), radio);
	}

}
