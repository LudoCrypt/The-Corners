package net.ludocrypt.limlib.impl.mixin;

import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Lifecycle;

import net.ludocrypt.limlib.api.LimlibRegistryHooks;
import net.ludocrypt.limlib.api.LimlibRegistryHooks.LimlibJsonRegistryHook;
import net.ludocrypt.limlib.api.LimlibRegistryHooks.LimlibRegistryHook;
import net.ludocrypt.limlib.api.LimlibWorld;
import net.ludocrypt.limlib.api.LimlibWorld.RegistryProvider;
import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
import net.ludocrypt.limlib.api.effects.sound.SoundEffects;
import net.ludocrypt.limlib.api.skybox.Skybox;
import net.ludocrypt.limlib.impl.SaveStorageSupplier;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.HolderProvider;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.ResourceFileNamespace;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Mixin(RegistryLoader.class)
public class RegistryLoaderMixin {

	@Shadow
	@Final
	@Mutable
	public static List<RegistryLoader.DecodingData<?>> WORLDGEN_REGISTRIES;
	static {
		List<RegistryLoader.DecodingData<?>> newRegistries = Lists.newArrayList();
		newRegistries.addAll(WORLDGEN_REGISTRIES);
		newRegistries.add(new RegistryLoader.DecodingData(PostEffect.POST_EFFECT_KEY, PostEffect.CODEC));
		newRegistries.add(new RegistryLoader.DecodingData(DimensionEffects.DIMENSION_EFFECTS_KEY, DimensionEffects.CODEC));
		newRegistries.add(new RegistryLoader.DecodingData(SoundEffects.SOUND_EFFECTS_KEY, SoundEffects.CODEC));
		newRegistries.add(new RegistryLoader.DecodingData(Skybox.SKYBOX_KEY, Skybox.CODEC));
		WORLDGEN_REGISTRIES = newRegistries;
	}

	@Inject(method = "loadRegistryContents(Lnet/minecraft/registry/RegistryOps$RegistryInfoLookup;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Decoder;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private static <E> void limlib$loadRegistryContents(RegistryOps.RegistryInfoLookup infoLookup,
			ResourceManager resourceManager, RegistryKey<? extends Registry<E>> registryKey, MutableRegistry<E> registry,
			Decoder<E> decoder, Map<RegistryKey<?>, Exception> readFailures, CallbackInfo ci, String string,
			ResourceFileNamespace resourceFileNamespace, RegistryOps<JsonElement> registryOps,
			Iterator<Map.Entry<Identifier, Resource>> var9, Map.Entry<Identifier, Resource> entry, Identifier identifier,
			RegistryKey<E> registryKey2, Resource resource, Reader reader, JsonElement jsonElement) {

		if (registryKey2.isOf(RegistryKeys.GENERATOR_TYPE)) {
			JsonObject presetType = jsonElement.getAsJsonObject();
			JsonObject dimensions = presetType.get("dimensions").getAsJsonObject();
			LimlibWorld.LIMLIB_WORLD
				.getEntries()
				.forEach((world) -> dimensions
					.add(world.getKey().getValue().toString(),
						DimensionOptions.CODEC
							.encodeStart(registryOps,
								world.getValue().getDimensionOptionsSupplier().apply(new RegistryProvider() {

									@Override
									public <T> HolderProvider<T> get(RegistryKey<Registry<T>> key) {
										return registryOps.getHolderProvider(key).get();
									}

								}))
							.result()
							.get()));
		}

		LimlibRegistryHooks.REGISTRY_JSON_HOOKS
			.getOrDefault(registryKey, Sets.newHashSet())
			.forEach((registrarhook -> ((LimlibJsonRegistryHook<E>) registrarhook)
				.register(infoLookup, registryKey, registryOps, jsonElement)));
	}

	@Inject(method = "loadRegistryContents(Lnet/minecraft/registry/RegistryOps$RegistryInfoLookup;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V", at = @At("TAIL"))
	private static <E> void limlib$loadRegistryContents(RegistryOps.RegistryInfoLookup infoLookup,
			ResourceManager resourceManager, RegistryKey<? extends Registry<E>> registryKey, MutableRegistry<E> registry,
			Decoder<E> decoder, Map<RegistryKey<?>, Exception> readFailures, CallbackInfo ci) {

		if (registryKey.equals(RegistryKeys.DIMENSION_TYPE)) {
			LimlibWorld.LIMLIB_WORLD
				.getEntries()
				.forEach((world) -> ((MutableRegistry<DimensionType>) registry)
					.register(RegistryKey.of(RegistryKeys.DIMENSION_TYPE, world.getKey().getValue()),
						world.getValue().getDimensionTypeSupplier().get(), Lifecycle.stable()));
		}

		LimlibRegistryHooks.REGISTRY_HOOKS
			.getOrDefault(registryKey, Sets.newHashSet())
			.forEach((registrarhook -> ((LimlibRegistryHook<E>) registrarhook).register(infoLookup, registryKey, registry)));
	}

	@Inject(method = "loadRegistriesIntoManager(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/DynamicRegistryManager;Ljava/util/List;)Lnet/minecraft/registry/DynamicRegistryManager$Frozen;", at = @At("TAIL"))
	private static void limlib$loadRegistriesIntoManager(ResourceManager resourceManager,
			DynamicRegistryManager registryManager, List<RegistryLoader.DecodingData<?>> decodingData,
			CallbackInfoReturnable<DynamicRegistryManager.Frozen> ci) {
		SaveStorageSupplier.LOADED_REGISTRY.set(registryManager.freeze());
	}

}
