package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.util.Identifier;

@Mixin(ModelLoader.class)
public class ModelLoaderMixin {

	@Inject(method = "Lnet/minecraft/client/render/model/ModelLoader;loadModel(Lnet/minecraft/util/Identifier;)V", at = @At("HEAD"), cancellable = true)
	private void corners$loadModel(Identifier id, CallbackInfo ci) {
		if (id.getNamespace().equals("corners") && id.getPath().startsWith("debug_")) {
			ci.cancel();
		}
	}

}
