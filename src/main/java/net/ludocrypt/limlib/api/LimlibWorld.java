package net.ludocrypt.limlib.api;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.HolderProvider;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

public class LimlibWorld {

	public static final RegistryKey<Registry<LimlibWorld>> LIMLIB_WORLD_KEY = RegistryKey
		.ofRegistry(new Identifier("limlib", "limlib_world"));
	public static final SimpleRegistry<LimlibWorld> LIMLIB_WORLD = FabricRegistryBuilder
		.createSimple(LIMLIB_WORLD_KEY)
		.attribute(RegistryAttribute.SYNCED)
		.buildAndRegister();

	private Supplier<DimensionType> dimensionTypeSupplier;
	private Function<RegistryProvider, DimensionOptions> dimensionOptionsSupplier;

	public LimlibWorld(Supplier<DimensionType> dimensionTypeSupplier,
			Function<RegistryProvider, DimensionOptions> dimensionOptionsSupplier) {
		this.dimensionTypeSupplier = Suppliers.memoize(dimensionTypeSupplier);
		this.dimensionOptionsSupplier = dimensionOptionsSupplier;
	}

	public Supplier<DimensionType> getDimensionTypeSupplier() {
		return dimensionTypeSupplier;
	}

	public Function<RegistryProvider, DimensionOptions> getDimensionOptionsSupplier() {
		return dimensionOptionsSupplier;
	}

	public static interface RegistryProvider {

		public <T> HolderProvider<T> get(RegistryKey<Registry<T>> key);

	}

	// Load the class early so our variables are set
	public static void load() {

	}

}
