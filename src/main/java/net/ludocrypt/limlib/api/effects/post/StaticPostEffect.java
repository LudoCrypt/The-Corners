package net.ludocrypt.limlib.api.effects.post;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;

public class StaticPostEffect extends PostEffect {

	public static final Codec<StaticPostEffect> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Identifier.CODEC.fieldOf("shader_name").stable().forGetter((postEffect) -> {
			return postEffect.shaderName;
		})).apply(instance, instance.stable(StaticPostEffect::new));
	});

	private final Identifier shaderName;

	public StaticPostEffect(Identifier shaderLocation) {
		this.shaderName = shaderLocation;
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
		return new Identifier(shaderName.getNamespace(), "shaders/post/" + shaderName.getPath() + ".json");
	}

}
