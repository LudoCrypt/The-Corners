package net.ludocrypt.limlib.impl;

import java.util.concurrent.atomic.AtomicReference;

import org.quiltmc.loader.api.ModInternal;

import net.minecraft.registry.DynamicRegistryManager;

public class SaveStorageSupplier {

	@ModInternal
	public static final AtomicReference<DynamicRegistryManager.Frozen> LOADED_REGISTRY = new AtomicReference<DynamicRegistryManager.Frozen>();

}
