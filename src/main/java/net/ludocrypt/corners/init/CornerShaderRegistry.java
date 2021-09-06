package net.ludocrypt.corners.init;

import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
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

	public static final SimpleRegistry<ManagedShaderEffect> SHADER_REGISTRY = FabricRegistryBuilder.createDefaulted(ManagedShaderEffect.class, TheCorners.id("shader_registry"), TheCorners.id("default_shader")).attribute(RegistryAttribute.SYNCED).buildAndRegister();
	public static final ManagedShaderEffect DEFAULT = ShaderEffectManager.getInstance().manage(new Identifier("shaders/post/empty_shader.json"));

	public static void init() {
		Registry.register(SHADER_REGISTRY, TheCorners.id("default_shader"), DEFAULT);
	}

	public static ManagedShaderEffect register(RegistryKey<World> world, ManagedShaderEffect shader) {
		return Registry.register(SHADER_REGISTRY, world.getValue(), shader);
	}

	public static ManagedShaderEffect getCurrent(MinecraftClient client) {
		return getCurrent(client.world.getRegistryKey());
	}

	public static ManagedShaderEffect getCurrent(RegistryKey<World> key) {
		return SHADER_REGISTRY.getOrEmpty(key.getValue()).orElse(DEFAULT);
	}

}
