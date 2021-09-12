package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.ludocrypt.corners.TheCorners;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class CornerShaderRegistry {

	public static final SimpleRegistry<Identifier> SHADER_REGISTRY = FabricRegistryBuilder.createDefaulted(Identifier.class, TheCorners.id("shader_registry"), TheCorners.id("default_shader")).attribute(RegistryAttribute.SYNCED).buildAndRegister();
	public static final Identifier DEFAULT = new Identifier("shaders/post/empty_shader.json");

	public static final Identifier YEARNING_CANAL_SHADER = get(CornerWorld.YEARNING_CANAL, CornerWorld.YEARNING_CANAL);

	public static void init() {
		Registry.register(SHADER_REGISTRY, TheCorners.id("default_shader"), DEFAULT);
	}

	public static Identifier register(RegistryKey<World> world, Identifier shader) {
		return Registry.register(SHADER_REGISTRY, world.getValue(), shader);
	}

	public static Identifier getCurrent(MinecraftClient client) {
		return getCurrent(client.world.getRegistryKey());
	}

	public static Identifier getCurrent(RegistryKey<World> key) {
		return SHADER_REGISTRY.getOrEmpty(key.getValue()).orElse(DEFAULT);
	}

}
