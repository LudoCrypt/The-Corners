package net.ludocrypt.limlib.impl.mixin.client;

import java.util.Optional;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.limlib.api.effects.LookupGrabber;
import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.ludocrypt.limlib.impl.shader.PostProcesser;
import net.ludocrypt.limlib.impl.shader.PostProcesserManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow
	@Final
	private MinecraftClient client;

	@Unique
	private final Function<Identifier, PostProcesser> memoizedShaders = Util
		.memoize(id -> PostProcesserManager.INSTANCE.find(id));

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawEntityOutlinesFramebuffer()V", shift = Shift.AFTER))
	private void limlib$render(float tickDelta, long nanoTime, boolean renderLevel, CallbackInfo info) {
		Optional<PostEffect> optionalPostEffect = LookupGrabber
			.snatch(client.world.getRegistryManager().getLookup(PostEffect.POST_EFFECT_KEY).get(),
				RegistryKey.of(PostEffect.POST_EFFECT_KEY, client.world.getRegistryKey().getValue()));

		if (optionalPostEffect.isPresent()) {
			PostEffect postEffect = optionalPostEffect.get();

			if (postEffect.shouldRender()) {
				postEffect.beforeRender();
				memoizedShaders.apply(postEffect.getShaderLocation()).render(tickDelta);
			}

		}

	}

}
