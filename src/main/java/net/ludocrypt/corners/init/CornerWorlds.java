package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import java.util.Optional;
import java.util.OptionalLong;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.client.render.StrongLiminalShader;
import net.ludocrypt.corners.config.CornerConfig;
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
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionType.MonsterSettings;

public class CornerWorlds implements Runnable {

	public static final LiminalEffects YEARNING_CANAL_EFFECTS = new LiminalEffects(Optional.of(new LiminalBaseEffects.SimpleBaseEffects(Optional.empty(), false, "NONE", true, false, false)), Optional.of(new LiminalShaderApplier.SimpleShader(TheCorners.id("yearning_canal"))), Optional.of(new LiminalSkyRenderer.SkyboxSky(TheCorners.id("textures/sky/yearning_canal"))), Optional.of(1.0F), Optional.of(new MusicSound(CornerSoundEvents.MUSIC_YEARNING_CANAL, 3000, 8000, true)), Optional.of(new ReverbSettings().setDecayTime(20.0F)));
	public static final LiminalEffects COMMUNAL_CORRIDORS_EFFECTS = new LiminalEffects(Optional.of(new LiminalBaseEffects.SimpleBaseEffects(Optional.empty(), false, "NONE", true, false, false)), Optional.of(new StrongLiminalShader(TheCorners.id("communal_corridors"))), Optional.of(new LiminalSkyRenderer.SkyboxSky(TheCorners.id("textures/sky/snow"))), Optional.of(1.0F), Optional.of(new MusicSound(CornerSoundEvents.MUSIC_COMMUNAL_CORRIDORS, 3000, 8000, true)), Optional.of(new ReverbSettings().setDecayTime(2.15F).setDensity(0.725F)));
	public static final LiminalEffects HOARY_CROSSROADS_EFFECTS = new LiminalEffects(Optional.of(new LiminalBaseEffects.SimpleBaseEffects(Optional.empty(), false, "NONE", true, false, true)), Optional.of(new LiminalShaderApplier.SimpleShader(TheCorners.id("hoary_crossroads"))), Optional.of(new LiminalSkyRenderer.SkyboxSky(TheCorners.id("textures/sky/hoary_crossroads"))), Optional.of(1.0F), Optional.of(new MusicSound(CornerSoundEvents.MUSIC_HOARY_CROSSROADS, 3000, 8000, true)), Optional.of(new ReverbSettings().setDecayTime(15.0F).setDensity(1.0F)));

	public static final LiminalWorld YEARNING_CANAL = get("yearning_canal", new LiminalWorld(TheCorners.id("yearning_canal"), YEARNING_CANAL_EFFECTS, () -> new DimensionType(OptionalLong.of(1200), true, false, false, true, 1.0, true, false, 0, CornerConfig.getInstance().condensedDimensions ? 432 : 2032, CornerConfig.getInstance().condensedDimensions ? 432 : 2032, TagKey.of(Registry.BLOCK_KEY, TheCorners.id("yearning_canal")), TheCorners.id("yearning_canal"), 1.0F, new MonsterSettings(false, false, ConstantIntProvider.ZERO, 0)), () -> new DimensionOptions(BuiltinRegistries.DIMENSION_TYPE.getOrCreateEntry(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, TheCorners.id("yearning_canal"))), new YearningCanalChunkGenerator(new FixedBiomeSource(BuiltinRegistries.BIOME.getOrCreateEntry(CornerBiomes.YEARNING_CANAL_BIOME))))));
	public static final LiminalWorld COMMUNAL_CORRIDORS = get("communal_corridors", new LiminalWorld(TheCorners.id("communal_corridors"), COMMUNAL_CORRIDORS_EFFECTS, () -> new DimensionType(OptionalLong.of(23500), true, false, false, true, 1.0, true, false, 0, 128, 128, TagKey.of(Registry.BLOCK_KEY, TheCorners.id("communal_corridors")), TheCorners.id("communal_corridors"), 0.075F, new MonsterSettings(false, false, ConstantIntProvider.ZERO, 0)), () -> new DimensionOptions(BuiltinRegistries.DIMENSION_TYPE.getOrCreateEntry(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, TheCorners.id("communal_corridors"))), new CommunalCorridorsChunkGenerator(new FixedBiomeSource(BuiltinRegistries.BIOME.getOrCreateEntry(CornerBiomes.COMMUNAL_CORRIDORS_BIOME))))));
	public static final LiminalWorld HOARY_CROSSROADS = get("hoary_crossroads", new LiminalWorld(TheCorners.id("hoary_crossroads"), HOARY_CROSSROADS_EFFECTS, () -> new DimensionType(OptionalLong.of(1200), true, false, false, true, 1.0, true, false, 0, 512, 512, TagKey.of(Registry.BLOCK_KEY, TheCorners.id("hoary_crossroads")), TheCorners.id("hoary_crossroads"), 0.725F, new MonsterSettings(false, false, ConstantIntProvider.ZERO, 0)), () -> new DimensionOptions(BuiltinRegistries.DIMENSION_TYPE.getOrCreateEntry(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, TheCorners.id("hoary_crossroads"))), new HoaryCrossroadsChunkGenerator(new FixedBiomeSource(BuiltinRegistries.BIOME.getOrCreateEntry(CornerBiomes.HOARY_CROSSROADS_BIOME)), new HoaryCrossroadsMazeGenerator(128, 128, 8, 0)))));

	@Override
	public void run() {

	}

}
