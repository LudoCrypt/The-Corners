package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

	@Invoker
	double callGetFov(Camera camera, float tickDelta, boolean changingFov);

	@Invoker
	void callBobViewWhenHurt(MatrixStack matrices, float tickDelta);

	@Invoker
	void callBobView(MatrixStack matrices, float tickDelta);

}
