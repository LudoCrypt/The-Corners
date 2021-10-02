package net.ludocrypt.corners.util;

import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Codec;

import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.client.sound.ReverbSettings;
import net.ludocrypt.corners.init.CornerRadioRegistry;
import net.ludocrypt.corners.init.CornerShaderRegistry;
import net.ludocrypt.corners.init.WorldReverbRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;

public class RegistryHelper {

	public static <T extends Block> T get(Identifier id, T block) {
		return Registry.register(Registry.BLOCK, id, block);
	}

	public static <T extends Block> T get(String id, T block) {
		if (id.startsWith("debug_") && FabricLoader.getInstance().isDevelopmentEnvironment()) {
			get(id, new BlockItem(block, new FabricItemSettings().group(ItemGroup.TOOLS)));
		}
		return Registry.register(Registry.BLOCK, TheCorners.id(id), block);
	}

	public static <T extends BlockEntity> BlockEntityType<T> get(String id, FabricBlockEntityTypeBuilder<T> builder) {
		Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, TheCorners.id(id), builder.build(type));
	}

	public static <T extends Block> T get(String id, T block, ItemGroup group) {
		get(id, new BlockItem(block, new FabricItemSettings().group(group)));
		return get(id, block);
	}

	public static <T extends Block> T get(String id, T block, FabricItemSettings settings) {
		get(id, new BlockItem(block, settings));
		return get(id, block);
	}

	public static <T extends Item> T get(String id, T item) {
		return Registry.register(Registry.ITEM, TheCorners.id(id), item);
	}

	public static <E extends Entity, T extends EntityType<E>> T get(String id, T entity) {
		return Registry.register(Registry.ENTITY_TYPE, TheCorners.id(id), entity);
	}

	public static <T extends PaintingMotive> T get(String id, T painting) {
		return Registry.register(Registry.PAINTING_MOTIVE, TheCorners.id(id), painting);
	}

	public static SoundEvent get(String id) {
		return get(id, new SoundEvent(TheCorners.id(id)));
	}

	public static <T extends SoundEvent> T get(String id, T sound) {
		return Registry.register(Registry.SOUND_EVENT, TheCorners.id(id), sound);
	}

	public static <T extends ReverbSettings> T get(String id, T reverb) {
		return Registry.register(WorldReverbRegistry.REVERB_REGISTRY, TheCorners.id(id), reverb);
	}

	public static ManagedShaderEffect get(String id, String shaderId) {
		return Registry.register(CornerShaderRegistry.SHADER_REGISTRY, TheCorners.id(id), ShaderEffectManager.getInstance().manage(new Identifier("shaders/post/" + shaderId + ".json")));
	}

	public static <T extends SoundEvent> T getRadio(String id, T radio) {
		return Registry.register(CornerRadioRegistry.RADIO_REGISTRY, TheCorners.id(id), radio);
	}

	public static <T extends Codec<? extends ChunkGenerator>> T get(String id, T item) {
		return Registry.register(Registry.CHUNK_GENERATOR, TheCorners.id(id), item);
	}

	public static RegistryKey<Biome> get(String id, Biome biome) {
		Registry.register(BuiltinRegistries.BIOME, TheCorners.id(id), biome);
		return RegistryKey.of(Registry.BIOME_KEY, TheCorners.id(id));
	}

	public static <SC extends SurfaceConfig, T extends ConfiguredSurfaceBuilder<SC>> T get(String id, T feature) {
		return Registry.register(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, id, feature);
	}

	public static <C extends SurfaceConfig, T extends SurfaceBuilder<C>> T get(String id, T feature) {
		return Registry.register(Registry.SURFACE_BUILDER, id, feature);
	}

	public static <FC extends FeatureConfig, F extends Feature<FC>, T extends ConfiguredFeature<FC, F>> T get(String id, T feature) {
		return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id, feature);
	}

	public static <FC extends FeatureConfig, T extends Feature<FC>> T get(String id, T feature) {
		return Registry.register(Registry.FEATURE, id, feature);
	}

	public static <DC extends DecoratorConfig, T extends Decorator<DC>> T get(String id, T decorator) {
		return Registry.register(Registry.DECORATOR, id, decorator);
	}

}
