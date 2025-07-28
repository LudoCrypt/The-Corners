package net.ludocrypt.limlib.impl.shader;

import java.util.Set;

import org.quiltmc.qsl.resource.loader.api.reloader.SimpleSynchronousResourceReloader;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public final class PostProcesserManager implements SimpleSynchronousResourceReloader {

	public static final PostProcesserManager INSTANCE = new PostProcesserManager();
	public static final Identifier RESOURCE_KEY = new Identifier("limlib:shaders");

	private final Set<PostProcesser> shaders = new ReferenceOpenHashSet<>();

	public PostProcesser find(Identifier location) {
		PostProcesser ret = new PostProcesser(location);
		shaders.add(ret);
		return ret;
	}

	public void onResolutionChanged(int newWidth, int newHeight) {

		if (!shaders.isEmpty()) {

			for (PostProcesser shader : shaders) {

				if (shader.isInitialized()) {
					MinecraftClient client = MinecraftClient.getInstance();
					shader.shader
						.setupDimensions(client.getWindow().getFramebufferWidth(),
							client.getWindow().getFramebufferHeight());
				}

			}

		}

	}

	@Override
	public Identifier getQuiltId() {
		return RESOURCE_KEY;
	}

	@Override
	public void reload(ResourceManager mgr) {

		for (PostProcesser shader : shaders) {
			shader.init(mgr);
		}

	}

	public void dispose(PostProcesser shader) {
		shader.release();
		shaders.remove(shader);
	}

}
