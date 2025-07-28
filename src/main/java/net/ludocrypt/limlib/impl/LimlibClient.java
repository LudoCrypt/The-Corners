package net.ludocrypt.limlib.impl;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;

import net.ludocrypt.limlib.impl.shader.PostProcesserManager;
import net.minecraft.resource.ResourceType;

public class LimlibClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(PostProcesserManager.INSTANCE);
	}

}
