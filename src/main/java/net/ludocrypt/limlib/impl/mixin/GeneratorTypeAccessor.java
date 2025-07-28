package net.ludocrypt.limlib.impl.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.world.GeneratorType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;

@Mixin(GeneratorType.class)
public interface GeneratorTypeAccessor {

	@Accessor
	Map<RegistryKey<DimensionOptions>, DimensionOptions> getDimensions();

	@Mutable
	@Accessor
	void setDimensions(Map<RegistryKey<DimensionOptions>, DimensionOptions> dimensions);

}
