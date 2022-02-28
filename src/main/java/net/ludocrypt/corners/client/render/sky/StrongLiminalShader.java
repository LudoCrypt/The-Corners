package net.ludocrypt.corners.client.render.sky;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.corners.config.CornerConfig;
import net.ludocrypt.limlib.api.render.LiminalShader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class StrongLiminalShader extends LiminalShader.SimpleShader {

	public static final Codec<StrongLiminalShader> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Identifier.CODEC.fieldOf("shader").stable().forGetter((shader) -> {
			return shader.getShaderId();
		})).apply(instance, instance.stable(StrongLiminalShader::new));
	});

	public StrongLiminalShader(Identifier shader) {
		super(shader);
	}

	@Override
	public boolean shouldRender(MinecraftClient client, float tickdelta) {
		return !CornerConfig.getInstance().disableStrongShaders;
	}

	@Override
	public Codec<? extends LiminalShader> getCodec() {
		return CODEC;
	}

}
