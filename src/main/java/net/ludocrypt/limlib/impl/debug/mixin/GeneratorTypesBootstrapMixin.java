package net.ludocrypt.limlib.impl.debug.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.limlib.impl.debug.DebugNbtChunkGenerator;
import net.ludocrypt.limlib.impl.debug.DebugWorld;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.registry.Holder;
import net.minecraft.registry.HolderProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

@Mixin(targets = "net/minecraft/client/world/GeneratorTypes$Bootstrap")
public abstract class GeneratorTypesBootstrapMixin {

	@Shadow
	private HolderProvider<Biome> biome;
	@Shadow
	private Holder<DimensionType> overworld;

	@Inject(method = "Lnet/minecraft/client/world/GeneratorTypes$Bootstrap;method_41600()V", at = @At("TAIL"))
	public void limlib$addDimensionOpions(CallbackInfo ci) {
		this
			.addDimensionGenerator(DebugWorld.DEBUG_KEY, new DimensionOptions(this.overworld,
				new DebugNbtChunkGenerator(this.biome.getHolderOrThrow(Biomes.THE_VOID))));
	}

	abstract void addDimensionGenerator(RegistryKey<GeneratorType> generator, DimensionOptions dimension);

}
