package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.ludocrypt.corners.access.BakedQuadAccess;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Vec3f;

@Mixin(BakedQuad.class)
public class BakedQuadMixin implements BakedQuadAccess {

	private Vec3f from;
	private Vec3f to;

	@Override
	public Vec3f getFrom() {
		return from;
	}

	@Override
	public Vec3f getTo() {
		return to;
	}

	@Override
	public void setFrom(Vec3f vec) {
		this.from = vec;
	}

	@Override
	public void setTo(Vec3f vec) {
		this.to = vec;
	}

}
