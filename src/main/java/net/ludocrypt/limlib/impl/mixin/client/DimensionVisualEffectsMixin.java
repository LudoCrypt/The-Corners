package net.ludocrypt.limlib.impl.mixin.client;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.limlib.api.effects.LookupGrabber;
import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
import net.minecraft.client.render.DimensionVisualEffects;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

@Mixin(DimensionVisualEffects.class)
public class DimensionVisualEffectsMixin {

	@Inject(method = "byDimensionType", at = @At("HEAD"), cancellable = true)
	private static void limlib$byDimensionType(DimensionType dimensionType,
			CallbackInfoReturnable<DimensionVisualEffects> ci) {
		Optional<DimensionEffects> dimensionEffects = LookupGrabber
			.snatch(DimensionEffects.MIXIN_WORLD_LOOKUP.get(),
				RegistryKey.of(DimensionEffects.DIMENSION_EFFECTS_KEY, dimensionType.effectsLocation()));

		if (dimensionEffects.isPresent()) {
			ci.setReturnValue(dimensionEffects.get().getDimensionEffects());
		}

	}

}
