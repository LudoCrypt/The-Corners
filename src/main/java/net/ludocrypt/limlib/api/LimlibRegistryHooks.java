package net.ludocrypt.limlib.api;

import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;

import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryOps.RegistryInfoLookup;

public class LimlibRegistryHooks {

	@Internal
	public static final Map<RegistryKey<? extends Registry<?>>, Set<LimlibRegistryHook<?>>> REGISTRY_HOOKS = Maps
		.newHashMap();
	@Internal
	public static final Map<RegistryKey<? extends Registry<?>>, Set<LimlibJsonRegistryHook<?>>> REGISTRY_JSON_HOOKS = Maps
		.newHashMap();

	public static <O, T extends Registry<O>> void hook(RegistryKey<T> key, LimlibRegistryHook<O> hook) {
		Set<LimlibRegistryHook<?>> hooks = REGISTRY_HOOKS.computeIfAbsent(key, k -> Sets.newHashSet());
		hooks.add(hook);
	}

	public static <O, T extends Registry<O>> void hook(RegistryKey<T> key, LimlibJsonRegistryHook<O> hook) {
		Set<LimlibJsonRegistryHook<?>> hooks = REGISTRY_JSON_HOOKS.computeIfAbsent(key, k -> Sets.newHashSet());
		hooks.add(hook);
	}

	@FunctionalInterface
	public interface LimlibRegistryHook<O> {

		/**
		 * @param infoLookup  The full registry lookup.
		 * @param registryKey The RegistryKey of the registry.
		 * @param registry    The MutableRegistry where to register.
		 */
		void register(RegistryInfoLookup infoLookup, RegistryKey<? extends Registry<O>> registryKey,
				MutableRegistry<O> registry);

	}

	@FunctionalInterface
	public interface LimlibJsonRegistryHook<O> {

		/**
		 * @param infoLookup  The full registry lookup.
		 * @param registryKey The RegistryKey of the registry.
		 * @param registry    The MutableRegistry where to register.
		 * @param jsonElement The jsonElement to modify before being read by a CODEC.
		 */
		void register(RegistryInfoLookup infoLookup, RegistryKey<? extends Registry<O>> registryKey,
				RegistryOps<JsonElement> registryOps, JsonElement jsonElement);

	}

}
