package net.ludocrypt.corners.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.corners.access.BakedQuadAccess;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

@Mixin(BakedQuadFactory.class)
public class BakedQuadFactoryMixin {

	@Inject(method = "bake", at = @At("TAIL"), cancellable = true)
	public void corners$bake(Vec3f from, Vec3f to, ModelElementFace face, Sprite texture, Direction side, ModelBakeSettings settings, @Nullable ModelRotation rotation, boolean shade, Identifier modelId, CallbackInfoReturnable<BakedQuad> ci) {
		BakedQuad quad = ci.getReturnValue();
		((BakedQuadAccess) quad).setFrom(from);
		((BakedQuadAccess) quad).setTo(to);
		ci.setReturnValue(quad);
	}

}
