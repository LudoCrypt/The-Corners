package net.ludocrypt.corners.access;

import net.minecraft.util.math.Vec3f;

public interface BakedQuadAccess {

	public Vec3f getFrom();

	public Vec3f getTo();

	public void setFrom(Vec3f vec);

	public void setTo(Vec3f vec);

}
