package net.ludocrypt.corners.client.render.sky;

import ladysnake.satin.api.managed.ManagedShaderEffect;
import net.ludocrypt.corners.config.CornerConfig;
import net.ludocrypt.limlib.api.render.LiminalShader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class StrongLiminalShader extends LiminalShader.SimpleShader {

	public StrongLiminalShader(Identifier shader) {
		super(shader);
	}

	public StrongLiminalShader(ManagedShaderEffect shader) {
		super(shader);
	}

	@Override
	public boolean shouldRender(MinecraftClient client, float tickdelta) {
		return !CornerConfig.getInstance().disableStrongShaders;
	}

}
