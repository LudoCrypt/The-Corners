package net.ludocrypt.corners.client.render;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.corners.config.CornerConfig;
import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.minecraft.util.Identifier;

public class StrongPostEffect extends PostEffect {

	public static final Codec<StrongPostEffect> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Identifier.CODEC.fieldOf("shader_name").stable().forGetter((postEffect) -> {
			return postEffect.shaderName;
		}), Identifier.CODEC.fieldOf("fallback_shader_name").stable().forGetter((postEffect) -> {
			return postEffect.fallbackShaderName;
		})).apply(instance, instance.stable(StrongPostEffect::new));
	});
	private final Identifier shaderName;
	private final Identifier fallbackShaderName;

	public StrongPostEffect(Identifier shaderName, Identifier fallbackShaderName) {
		this.shaderName = shaderName;
		this.fallbackShaderName = fallbackShaderName;
	}

	@Override
	public Codec<? extends PostEffect> getCodec() {
		return CODEC;
	}

	@Override
	public boolean shouldRender() {
		return true;
	}

	@Override
	public void beforeRender() {
	}

	@Override
	public Identifier getShaderLocation() {
		return CornerConfig.get().disableStrongShaders ? this.getFallbackShaderLocation() : this.getStrongShaderLocation();
	}

	public Identifier getStrongShaderLocation() {
		return new Identifier(shaderName.getNamespace(), "shaders/post/" + shaderName.getPath() + ".json");
	}

	public Identifier getFallbackShaderLocation() {
		return new Identifier(fallbackShaderName.getNamespace(), "shaders/post/" + fallbackShaderName.getPath() + ".json");
	}

}
