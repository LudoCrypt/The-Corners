package net.ludocrypt.limlib.impl.mixin.client;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Holder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World {

	protected ClientWorldMixin(MutableWorldProperties worldProperties, RegistryKey<World> registryKey,
			DynamicRegistryManager registryManager, Holder<DimensionType> dimension, Supplier<Profiler> profiler,
			boolean client, boolean debug, long seed, int maxChainedNeighborUpdates) {
		super(worldProperties, registryKey, registryManager, dimension, profiler, client, debug, seed,
			maxChainedNeighborUpdates);
	}

	@Inject(method = "<init>(Lnet/minecraft/client/network/ClientPlayNetworkHandler;Lnet/minecraft/client/world/ClientWorld$Properties;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/Holder;IILjava/util/function/Supplier;Lnet/minecraft/client/render/WorldRenderer;ZJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/DimensionVisualEffects;byDimensionType(Lnet/minecraft/world/dimension/DimensionType;)Lnet/minecraft/client/render/DimensionVisualEffects;", shift = Shift.BEFORE))
	private void limlib$init(ClientPlayNetworkHandler netHandler, ClientWorld.Properties clientWorldProperties,
			RegistryKey<World> registryKey, Holder<DimensionType> dimensionType, int chunkManager, int simulationDistance,
			Supplier<Profiler> profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed, CallbackInfo ci) {
		DimensionEffects.MIXIN_WORLD_LOOKUP
			.set(this.getRegistryManager().getLookup(DimensionEffects.DIMENSION_EFFECTS_KEY).get());
	}

}
