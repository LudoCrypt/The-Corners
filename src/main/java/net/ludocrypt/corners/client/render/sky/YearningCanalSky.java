package net.ludocrypt.corners.client.render.sky;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.math.Vec3d;

public class YearningCanalSky extends SkyProperties {

	public YearningCanalSky() {
		super(Float.NaN, false, SkyType.NONE, true, false);
	}

	@Override
	public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
		return color;
	}

	@Override
	public boolean useThickFog(int camX, int camY) {
		return false;
	}

	@Nullable
	public float[] getFogColorOverride(float skyAngle, float tickDelta) {
		return null;
	}

}
