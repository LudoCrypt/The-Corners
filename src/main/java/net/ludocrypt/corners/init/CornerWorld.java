package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;
import static net.ludocrypt.corners.util.RegistryHelper.getMaze;
import static net.ludocrypt.corners.util.RegistryHelper.getRadio;

import java.util.Optional;
import java.util.OptionalLong;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.client.render.sky.StrongLiminalShader;
import net.ludocrypt.corners.config.CornerConfig;
import net.ludocrypt.corners.world.biome.CommunalCorridorsBiome;
import net.ludocrypt.corners.world.biome.HoaryCrossroadsBiome;
import net.ludocrypt.corners.world.biome.YearningCanalBiome;
import net.ludocrypt.corners.world.chunk.CommunalCorridorsChunkGenerator;
import net.ludocrypt.corners.world.chunk.HoaryCrossroadsChunkGenerator;
import net.ludocrypt.corners.world.chunk.YearningCanalChunkGenerator;
import net.ludocrypt.corners.world.maze.HoaryCrossroadsMazeGenerator;
import net.ludocrypt.limlib.api.LiminalEffects;
import net.ludocrypt.limlib.api.LiminalWorld;
import net.ludocrypt.limlib.api.render.LiminalBaseEffects;
import net.ludocrypt.limlib.api.render.LiminalShaderApplier;
import net.ludocrypt.limlib.api.render.LiminalSkyRenderer;
import net.ludocrypt.limlib.api.sound.ReverbSettings;
import net.minecraft.sound.MusicSound;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

public class CornerWorld {

	public static final RegistryKey<Biome> YEARNING_CANAL_BIOME = get("yearning_canal", YearningCanalBiome.create());
	public static final RegistryKey<Biome> COMMUNAL_CORRIDORS_BIOME = get("communal_corridors", CommunalCorridorsBiome.create());
	public static final RegistryKey<Biome> HOARY_CROSSROADS_BIOME = get("hoary_crossroads", HoaryCrossroadsBiome.create());

	public static final LiminalEffects YEARNING_CANAL_EFFECTS = new LiminalEffects(Optional.of(new LiminalBaseEffects.SimpleBaseEffects(Optional.empty(), false, "NONE", true, false, false)), Optional.of(new LiminalShaderApplier.SimpleShader(TheCorners.id("yearning_canal"))), Optional.of(new LiminalSkyRenderer.SkyboxSky(TheCorners.id("textures/sky/yearning_canal"))), Optional.of(new MusicSound(CornerSoundEvents.MUSIC_YEARNING_CANAL, 3000, 8000, true)), Optional.of(new ReverbSettings().setDecayTime(20.0F)));
	public static final LiminalEffects COMMUNAL_CORRIDORS_EFFECTS = new LiminalEffects(Optional.of(new LiminalBaseEffects.SimpleBaseEffects(Optional.empty(), false, "NONE", true, false, false)), Optional.of(new StrongLiminalShader(TheCorners.id("communal_corridors"))), Optional.of(new LiminalSkyRenderer.SkyboxSky(TheCorners.id("textures/sky/snow"))), Optional.of(new MusicSound(CornerSoundEvents.MUSIC_COMMUNAL_CORRIDORS, 3000, 8000, true)), Optional.of(new ReverbSettings().setDecayTime(2.15F).setDensity(0.725F)));
	public static final LiminalEffects HOARY_CROSSROADS_EFFECTS = new LiminalEffects(Optional.of(new LiminalBaseEffects.SimpleBaseEffects(Optional.empty(), false, "NONE", true, false, true)), Optional.of(new LiminalShaderApplier.SimpleShader(TheCorners.id("hoary_crossroads"))), Optional.of(new LiminalSkyRenderer.SkyboxSky(TheCorners.id("textures/sky/hoary_crossroads"))), Optional.of(new MusicSound(CornerSoundEvents.MUSIC_HOARY_CROSSROADS, 3000, 8000, true)), Optional.of(new ReverbSettings().setDecayTime(15.0F).setDensity(1.0F)));

	public static final LiminalWorld YEARNING_CANAL = get("yearning_canal", new LiminalWorld(TheCorners.id("yearning_canal"), DimensionType.create(OptionalLong.of(1200), true, false, false, true, 1.0, false, false, true, false, false, 0, CornerConfig.getInstance().condensedDimensions ? 432 : 2032, CornerConfig.getInstance().condensedDimensions ? 432 : 2032, TagKey.of(Registry.BLOCK_KEY, TheCorners.id("yearning_canal")), TheCorners.id("yearning_canal"), 1.0F), (world, dimensionTypeRegistry, biomeRegistry, structureRegistry, chunkGeneratorSettingsRegistry, noiseSettingsRegistry, registryManager, seed) -> new DimensionOptions(dimensionTypeRegistry.getOrCreateEntry(world.getDimensionTypeKey()), new YearningCanalChunkGenerator(new FixedBiomeSource(biomeRegistry.getOrCreateEntry(CornerWorld.YEARNING_CANAL_BIOME)), seed)), YEARNING_CANAL_EFFECTS));
	public static final LiminalWorld COMMUNAL_CORRIDORS = get("communal_corridors", new LiminalWorld(TheCorners.id("communal_corridors"), DimensionType.create(OptionalLong.of(23500), true, false, false, true, 1.0, false, false, true, false, false, 0, 128, 128, TagKey.of(Registry.BLOCK_KEY, TheCorners.id("communal_corridors")), TheCorners.id("communal_corridors"), 0.075F), (world, dimensionTypeRegistry, biomeRegistry, structureRegistry, chunkGeneratorSettingsRegistry, noiseSettingsRegistry, registryManager, seed) -> new DimensionOptions(dimensionTypeRegistry.getOrCreateEntry(world.getDimensionTypeKey()), new CommunalCorridorsChunkGenerator(new FixedBiomeSource(biomeRegistry.getOrCreateEntry(CornerWorld.COMMUNAL_CORRIDORS_BIOME)), seed)), COMMUNAL_CORRIDORS_EFFECTS));
	public static final LiminalWorld HOARY_CROSSROADS = get("hoary_crossroads", new LiminalWorld(TheCorners.id("hoary_crossroads"), DimensionType.create(OptionalLong.of(1200), true, false, false, true, 1.0, false, false, true, false, false, 0, 512, 512, TagKey.of(Registry.BLOCK_KEY, TheCorners.id("hoary_crossroads")), TheCorners.id("hoary_crossroads"), 0.725F), (world, dimensionTypeRegistry, biomeRegistry, structureRegistry, chunkGeneratorSettingsRegistry, noiseSettingsRegistry, registryManager, seed) -> new DimensionOptions(dimensionTypeRegistry.getOrCreateEntry(world.getDimensionTypeKey()), new HoaryCrossroadsChunkGenerator(new FixedBiomeSource(biomeRegistry.getOrCreateEntry(CornerWorld.HOARY_CROSSROADS_BIOME)), seed, new HoaryCrossroadsMazeGenerator(32, 32, seed))), HOARY_CROSSROADS_EFFECTS));

	public static void init() {
		get("yearning_canal_chunk_generator", YearningCanalChunkGenerator.CODEC);
		get("communal_corridors_chunk_generator", CommunalCorridorsChunkGenerator.CODEC);
		get("hoary_crossroads_chunk_generator", HoaryCrossroadsChunkGenerator.CODEC);
		getMaze("hoary_crossroads_maze_generator", HoaryCrossroadsMazeGenerator.CODEC);
		getRadio(YEARNING_CANAL.getIdentifier().getPath(), CornerSoundEvents.RADIO_YEARNING_CANAL);
		getRadio(COMMUNAL_CORRIDORS.getIdentifier().getPath(), CornerSoundEvents.RADIO_COMMUNAL_CORRIDORS);
		getRadio(HOARY_CROSSROADS.getIdentifier().getPath(), CornerSoundEvents.RADIO_HOARY_CROSSROADS);
	}

}
