package net.ludocrypt.corners.init;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.client.render.StrongPostEffect;
import net.ludocrypt.corners.world.biome.CommunalCorridorsBiome;
import net.ludocrypt.corners.world.biome.HoaryCrossroadsBiome;
import net.ludocrypt.corners.world.biome.YearningCanalBiome;
import net.ludocrypt.corners.world.chunk.CommunalCorridorsChunkGenerator;
import net.ludocrypt.corners.world.chunk.HoaryCrossroadsChunkGenerator;
import net.ludocrypt.corners.world.chunk.YearningCanalChunkGenerator;
import net.ludocrypt.corners.world.feature.GaiaTreeFeature;
import net.ludocrypt.limlib.api.LimlibRegistrar;
import net.ludocrypt.limlib.api.LimlibRegistryHooks;
import net.ludocrypt.limlib.api.LimlibWorld;
import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.ludocrypt.limlib.api.effects.post.StaticPostEffect;
import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
import net.ludocrypt.limlib.api.effects.sky.StaticDimensionEffects;
import net.ludocrypt.limlib.api.effects.sound.SoundEffects;
import net.ludocrypt.limlib.api.effects.sound.reverb.StaticReverbEffect;
import net.ludocrypt.limlib.api.skybox.Skybox;
import net.ludocrypt.limlib.api.skybox.TexturedSkybox;
import net.minecraft.registry.HolderProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.math.int_provider.ConstantIntProvider;
import net.minecraft.util.math.int_provider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionType.MonsterSettings;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.MegaPineFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.GiantTrunkPlacer;

public class CornerWorlds implements LimlibRegistrar {

	private static final List<Pair<RegistryKey<LimlibWorld>, LimlibWorld>> WORLDS = Lists.newArrayList();
	private static final List<Pair<RegistryKey<SoundEffects>, SoundEffects>> SOUND_EFFECTS = Lists.newArrayList();
	private static final List<Pair<RegistryKey<Skybox>, Skybox>> SKYBOXES = Lists.newArrayList();
	private static final List<Pair<RegistryKey<DimensionEffects>, DimensionEffects>> DIMENSION_EFFECTS = Lists
		.newArrayList();
	private static final List<Pair<RegistryKey<PostEffect>, PostEffect>> POST_EFFECTS = Lists.newArrayList();
	public static final String YEARNING_CANAL = "yearning_canal";
	public static final String COMMUNAL_CORRIDORS = "communal_corridors";
	public static final String HOARY_CROSSROADS = "hoary_crossroads";
	public static final RegistryKey<World> YEARNING_CANAL_KEY = RegistryKey
		.of(RegistryKeys.WORLD, TheCorners.id(YEARNING_CANAL));
	public static final RegistryKey<World> COMMUNAL_CORRIDORS_KEY = RegistryKey
		.of(RegistryKeys.WORLD, TheCorners.id(COMMUNAL_CORRIDORS));
	public static final RegistryKey<World> HOARY_CROSSROADS_KEY = RegistryKey
		.of(RegistryKeys.WORLD, TheCorners.id(HOARY_CROSSROADS));

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void registerHooks() {
		// Sound Effects
		get(YEARNING_CANAL, new SoundEffects(Optional.of(new StaticReverbEffect.Builder().setDecayTime(20.0F).build()),
			Optional.empty(), Optional.of(new MusicSound(CornerSoundEvents.MUSIC_YEARNING_CANAL, 3000, 8000, true))));
		get(COMMUNAL_CORRIDORS,
			new SoundEffects(Optional.of(new StaticReverbEffect.Builder().setDecayTime(2.15F).setDensity(0.0725F).build()),
				Optional.empty(), Optional.empty()));
		get(HOARY_CROSSROADS,
			new SoundEffects(Optional.of(new StaticReverbEffect.Builder().setDecayTime(15.0F).setDensity(1.0F).build()),
				Optional.empty(), Optional.of(new MusicSound(CornerSoundEvents.MUSIC_HOARY_CROSSROADS, 3000, 8000, true))));
		// Skyboxes
		get(YEARNING_CANAL, new TexturedSkybox(TheCorners.id("textures/sky/yearning_canal")));
		get(COMMUNAL_CORRIDORS, new TexturedSkybox(TheCorners.id("textures/sky/snow")));
		get(HOARY_CROSSROADS, new TexturedSkybox(TheCorners.id("textures/sky/hoary_crossroads")));
		// Sky Effects
		get(YEARNING_CANAL, new StaticDimensionEffects(Optional.empty(), false, "NONE", true, false, false, 1.0F));
		get(COMMUNAL_CORRIDORS, new StaticDimensionEffects(Optional.empty(), false, "NONE", true, false, false, 1.0F));
		get(HOARY_CROSSROADS, new StaticDimensionEffects(Optional.empty(), false, "NONE", true, false, true, 1.0F));
		// Post Effects
		get(YEARNING_CANAL, new StaticPostEffect(TheCorners.id(YEARNING_CANAL)));
		get(COMMUNAL_CORRIDORS,
			new StrongPostEffect(TheCorners.id(COMMUNAL_CORRIDORS), TheCorners.id(COMMUNAL_CORRIDORS + "_fallback")));
		get(HOARY_CROSSROADS, new StaticPostEffect(TheCorners.id(HOARY_CROSSROADS)));
		// Worlds
		get(YEARNING_CANAL,
			new LimlibWorld(
				() -> new DimensionType(OptionalLong.of(1200), true, false, false, true, 1.0, true, false, 0, 2032, 2032,
					TagKey.of(RegistryKeys.BLOCK, TheCorners.id(YEARNING_CANAL)), TheCorners.id(YEARNING_CANAL), 1.0F,
					new MonsterSettings(false, false, ConstantIntProvider.ZERO, 0)),
				(registry) -> new DimensionOptions(
					registry
						.get(RegistryKeys.DIMENSION_TYPE)
						.getHolder(RegistryKey.of(RegistryKeys.DIMENSION_TYPE, TheCorners.id(YEARNING_CANAL)))
						.get(),
					new YearningCanalChunkGenerator(
						new FixedBiomeSource(
							registry.get(RegistryKeys.BIOME).getHolder(CornerBiomes.YEARNING_CANAL_BIOME).get()),
						YearningCanalChunkGenerator.createGroup()))));
		get(COMMUNAL_CORRIDORS,
			new LimlibWorld(
				() -> new DimensionType(OptionalLong.of(23500), true, false, false, true, 1.0, true, false, 0, 256, 256,
					TagKey.of(RegistryKeys.BLOCK, TheCorners.id(COMMUNAL_CORRIDORS)), TheCorners.id(COMMUNAL_CORRIDORS),
					0.075F, new MonsterSettings(false, false, ConstantIntProvider.ZERO, 0)),
				(registry) -> new DimensionOptions(
					registry
						.get(RegistryKeys.DIMENSION_TYPE)
						.getHolder(RegistryKey.of(RegistryKeys.DIMENSION_TYPE, TheCorners.id(COMMUNAL_CORRIDORS)))
						.get(),
					new CommunalCorridorsChunkGenerator(
						new FixedBiomeSource(
							registry.get(RegistryKeys.BIOME).getHolder(CornerBiomes.COMMUNAL_CORRIDORS_BIOME).get()),
						CommunalCorridorsChunkGenerator.createGroup(), 16, 16, 8, 0))));
		get(HOARY_CROSSROADS,
			new LimlibWorld(
				() -> new DimensionType(OptionalLong.of(1200), true, false, false, true, 1.0, true, false, 0, 512, 512,
					TagKey.of(RegistryKeys.BLOCK, TheCorners.id(HOARY_CROSSROADS)), TheCorners.id(HOARY_CROSSROADS), 0.725F,
					new MonsterSettings(false, false, ConstantIntProvider.ZERO, 0)),
				(registry) -> new DimensionOptions(
					registry
						.get(RegistryKeys.DIMENSION_TYPE)
						.getHolder(RegistryKey.of(RegistryKeys.DIMENSION_TYPE, TheCorners.id(HOARY_CROSSROADS)))
						.get(),
					new HoaryCrossroadsChunkGenerator(
						new FixedBiomeSource(
							registry.get(RegistryKeys.BIOME).getHolder(CornerBiomes.HOARY_CROSSROADS_BIOME).get()),
						HoaryCrossroadsChunkGenerator.createGroup(), 16, 16, 4, 0))));
		// Registries
		WORLDS.forEach((pair) -> LimlibWorld.LIMLIB_WORLD.register(pair.getFirst(), pair.getSecond(), Lifecycle.stable()));
		LimlibRegistryHooks
			.hook(SoundEffects.SOUND_EFFECTS_KEY, (infoLookup, registryKey, registry) -> SOUND_EFFECTS
				.forEach((pair) -> registry.register(pair.getFirst(), pair.getSecond(), Lifecycle.stable())));
		LimlibRegistryHooks
			.hook(Skybox.SKYBOX_KEY, (infoLookup, registryKey, registry) -> SKYBOXES
				.forEach((pair) -> registry.register(pair.getFirst(), pair.getSecond(), Lifecycle.stable())));
		LimlibRegistryHooks
			.hook(DimensionEffects.DIMENSION_EFFECTS_KEY, (infoLookup, registryKey, registry) -> DIMENSION_EFFECTS
				.forEach((pair) -> registry.register(pair.getFirst(), pair.getSecond(), Lifecycle.stable())));
		LimlibRegistryHooks
			.hook(PostEffect.POST_EFFECT_KEY, (infoLookup, registryKey, registry) -> POST_EFFECTS
				.forEach((pair) -> registry.register(pair.getFirst(), pair.getSecond(), Lifecycle.stable())));
		LimlibRegistryHooks.hook(RegistryKeys.BIOME, (infoLookup, registryKey, registry) -> {
			HolderProvider<PlacedFeature> features = infoLookup.lookup(RegistryKeys.PLACED_FEATURE).get().getter();
			HolderProvider<ConfiguredCarver<?>> carvers = infoLookup.lookup(RegistryKeys.CONFIGURED_CARVER).get().getter();
			registry
				.register(CornerBiomes.YEARNING_CANAL_BIOME, YearningCanalBiome.create(features, carvers),
					Lifecycle.stable());
			registry
				.register(CornerBiomes.COMMUNAL_CORRIDORS_BIOME, CommunalCorridorsBiome.create(features, carvers),
					Lifecycle.stable());
			registry
				.register(CornerBiomes.HOARY_CROSSROADS_BIOME, HoaryCrossroadsBiome.create(features, carvers),
					Lifecycle.stable());
		});
		LimlibRegistryHooks.hook(RegistryKeys.FEATURE, (infoLookup, registryKey, registry) -> {
			registry
				.register(CornerBiomes.GAIA_TREE_FEATURE, new GaiaTreeFeature(DefaultFeatureConfig.CODEC),
					Lifecycle.stable());
		});
		LimlibRegistryHooks.hook(RegistryKeys.CONFIGURED_FEATURE, (infoLookup, registryKey, registry) -> {
			registry
				.register(CornerBiomes.CONFIGURED_GAIA_TREE_FEATURE,
					new ConfiguredFeature<DefaultFeatureConfig, GaiaTreeFeature>(
						new GaiaTreeFeature(DefaultFeatureConfig.CODEC), DefaultFeatureConfig.INSTANCE),
					Lifecycle.stable());
			registry
				.register(CornerBiomes.CONFIGURED_SAPLING_GAIA_TREE_FEATURE,
					new ConfiguredFeature(Feature.TREE,
						new TreeFeatureConfig.Builder(BlockStateProvider.of(CornerBlocks.GAIA_LOG),
							new GiantTrunkPlacer(10, 5, 5), BlockStateProvider.of(CornerBlocks.GAIA_LEAVES),
							new MegaPineFoliagePlacer(ConstantIntProvider.create(0), ConstantIntProvider.create(0),
								UniformIntProvider.create(8, 10)),
							new TwoLayersFeatureSize(1, 1, 2)).build()),
					Lifecycle.stable());
		});
	}

	private static <W extends LimlibWorld> W get(String id, W world) {
		WORLDS.add(Pair.of(RegistryKey.of(LimlibWorld.LIMLIB_WORLD_KEY, TheCorners.id(id)), world));
		return world;
	}

	private static <S extends SoundEffects> S get(String id, S soundEffects) {
		SOUND_EFFECTS.add(Pair.of(RegistryKey.of(SoundEffects.SOUND_EFFECTS_KEY, TheCorners.id(id)), soundEffects));
		return soundEffects;
	}

	private static <S extends Skybox> S get(String id, S skybox) {
		SKYBOXES.add(Pair.of(RegistryKey.of(Skybox.SKYBOX_KEY, TheCorners.id(id)), skybox));
		return skybox;
	}

	private static <D extends DimensionEffects> D get(String id, D dimensionEffects) {
		DIMENSION_EFFECTS
			.add(Pair.of(RegistryKey.of(DimensionEffects.DIMENSION_EFFECTS_KEY, TheCorners.id(id)), dimensionEffects));
		return dimensionEffects;
	}

	private static <P extends PostEffect> P get(String id, P postEffect) {
		POST_EFFECTS.add(Pair.of(RegistryKey.of(PostEffect.POST_EFFECT_KEY, TheCorners.id(id)), postEffect));
		return postEffect;
	}

}
