package net.ludocrypt.limlib.api.skybox;

import java.util.function.Function;

import org.joml.Matrix4f;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import net.ludocrypt.limlib.impl.mixin.RegistriesAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public abstract class Skybox {

	public static final RegistryKey<Registry<Codec<? extends Skybox>>> SKYBOX_CODEC_KEY = RegistryKey
		.ofRegistry(new Identifier("limlib/codec/skybox"));
	public static final Registry<Codec<? extends Skybox>> SKYBOX_CODEC = RegistriesAccessor
		.callRegisterSimple(SKYBOX_CODEC_KEY, Lifecycle.stable(), (registry) -> TexturedSkybox.CODEC);
	public static final Codec<Skybox> CODEC = SKYBOX_CODEC.getCodec().dispatchStable(Skybox::getCodec, Function.identity());
	public static final RegistryKey<Registry<Skybox>> SKYBOX_KEY = RegistryKey.ofRegistry(new Identifier("limlib/skybox"));

	public abstract Codec<? extends Skybox> getCodec();

	public static void init() {
		Registry.register(Skybox.SKYBOX_CODEC, new Identifier("limlib", "empty"), EmptySkybox.CODEC);
		Registry.register(Skybox.SKYBOX_CODEC, new Identifier("limlib", "textured"), TexturedSkybox.CODEC);
	}

	@ClientOnly
	public abstract void renderSky(WorldRenderer worldRenderer, MinecraftClient client, MatrixStack matrices,
			Matrix4f projectionMatrix, float tickDelta);

}
