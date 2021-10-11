package net.ludocrypt.corners.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.ludocrypt.corners.init.CornerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;

@Mixin(DimensionOptions.class)
public class DimensionOptionsMixin {

	@Shadow
	@Final
	private static Set<RegistryKey<DimensionOptions>> BASE_DIMENSIONS;

	static {
		BASE_DIMENSIONS.add(CornerWorld.YEARNING_CANAL_DIMENSION_OPTOINS_REGISTRY_KEY);
		BASE_DIMENSIONS.add(CornerWorld.COMMUNAL_CORRIDORS_DIMENSION_OPTOINS_REGISTRY_KEY);
	}

}
