package net.ludocrypt.limlib.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.limlib.api.LimlibWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.dimension.BuiltinDimensionTypes;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.BootstrapContext;

@Mixin(BuiltinDimensionTypes.class)
public class BuiltinDimensionTypesMixin {

	@Inject(method = "bootstrap(Lnet/minecraft/world/gen/BootstrapContext;)V", at = @At("RETURN"))
	private static void limlib$initAndGetDefault(BootstrapContext<DimensionType> bootstrapContext, CallbackInfo ci) {
		LimlibWorld.LIMLIB_WORLD
			.getEntries()
			.forEach((entry) -> bootstrapContext
				.register(RegistryKey.of(RegistryKeys.DIMENSION_TYPE, entry.getKey().getValue()),
					entry.getValue().getDimensionTypeSupplier().get()));
	}

}
