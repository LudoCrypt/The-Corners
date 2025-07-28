package net.ludocrypt.limlib.impl.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Sets;

import net.ludocrypt.limlib.api.LimlibWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.WorldDimensions;

@Mixin(WorldDimensions.class)
public class WorldDimensionsMixin {

	@Shadow
	@Final
	@Mutable
	private static Set<RegistryKey<DimensionOptions>> VANILLA_DIMENSION_KEYS;

	@Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/Set;size()I", shift = Shift.BEFORE, ordinal = 0))
	private static void limlib$clinit(CallbackInfo ci) {
		Set<RegistryKey<DimensionOptions>> dimensions = Sets.newHashSet();
		dimensions.addAll(VANILLA_DIMENSION_KEYS);
		LimlibWorld.LIMLIB_WORLD
			.getEntries()
			.forEach((entry) -> dimensions.add(RegistryKey.of(RegistryKeys.DIMENSION, entry.getKey().getValue())));
		VANILLA_DIMENSION_KEYS = dimensions;
	}

}
