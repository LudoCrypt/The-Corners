package net.ludocrypt.corners.init;

import java.util.Optional;
import java.util.OptionalLong;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.client.render.StrongPostEffect;
import net.ludocrypt.corners.world.chunk.CommunalCorridorsChunkGenerator;
import net.ludocrypt.corners.world.chunk.HoaryCrossroadsChunkGenerator;
import net.ludocrypt.corners.world.chunk.YearningCanalChunkGenerator;
import net.ludocrypt.limlib.effects.render.post.PostEffect;
import net.ludocrypt.limlib.effects.render.post.StaticPostEffect;
import net.ludocrypt.limlib.effects.render.sky.SkyEffects;
import net.ludocrypt.limlib.effects.render.sky.StaticSkyEffects;
import net.ludocrypt.limlib.effects.sound.SoundEffects;
import net.ludocrypt.limlib.effects.sound.reverb.StaticReverbEffect;
import net.ludocrypt.limlib.registry.registration.LimlibWorld;
import net.ludocrypt.limlib.registry.registration.PreRegistration;
import net.ludocrypt.limlib.render.skybox.Skybox;
import net.ludocrypt.limlib.render.skybox.TexturedSkybox;
import net.minecraft.sound.MusicSound;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionType.MonsterSettings;

public class CornerWorlds implements PreRegistration {

	public static final String YEARNING_CANAL = "yearning_canal";
	public static final String COMMUNAL_CORRIDORS = "communal_corridors";
	public static final String HOARY_CROSSROADS = "hoary_crossroads";

	public static final RegistryKey<World> YEARNING_CANAL_KEY = RegistryKey.of(Registry.WORLD_KEY, TheCorners.id(YEARNING_CANAL));
	public static final RegistryKey<World> COMMUNAL_CORRIDORS_KEY = RegistryKey.of(Registry.WORLD_KEY, TheCorners.id(COMMUNAL_CORRIDORS));
	public static final RegistryKey<World> HOARY_CROSSROADS_KEY = RegistryKey.of(Registry.WORLD_KEY, TheCorners.id(HOARY_CROSSROADS));

	public static final SoundEffects YEARNING_CANAL_SOUND_EFFECTS = get(YEARNING_CANAL, new SoundEffects(Optional.of(new StaticReverbEffect.Builder().setDecayTime(20.0F).build()), Optional.empty(), Optional.of(new MusicSound(CornerSoundEvents.MUSIC_YEARNING_CANAL, 3000, 8000, true))));
	public static final SoundEffects COMMUNAL_CORRIDORS_SOUND_EFFECTS = get(COMMUNAL_CORRIDORS, new SoundEffects(Optional.of(new StaticReverbEffect.Builder().setDecayTime(2.15F).setDensity(0.0725F).build()), Optional.empty(), Optional.of(new MusicSound(CornerSoundEvents.MUSIC_COMMUNAL_CORRIDORS, 3000, 8000, true))));
	public static final SoundEffects HOARY_CROSSROADS_SOUND_EFFECTS = get(HOARY_CROSSROADS, new SoundEffects(Optional.of(new StaticReverbEffect.Builder().setDecayTime(15.0F).setDensity(1.0F).build()), Optional.empty(), Optional.of(new MusicSound(CornerSoundEvents.MUSIC_HOARY_CROSSROADS, 3000, 8000, true))));

	public static final Skybox YEARNING_CANAL_SKYBOX = get(YEARNING_CANAL, new TexturedSkybox(TheCorners.id("textures/sky/yearning_canal")));
	public static final Skybox COMMUNAL_CORRIDORS_SKYBOX = get(COMMUNAL_CORRIDORS, new TexturedSkybox(TheCorners.id("textures/sky/snow")));
	public static final Skybox HOARY_CROSSROADS_SKYBOX = get(HOARY_CROSSROADS, new TexturedSkybox(TheCorners.id("textures/sky/hoary_crossroads")));

	public static final SkyEffects YEARNING_CANAL_SKY_EFFECTS = get(YEARNING_CANAL, new StaticSkyEffects(Optional.empty(), false, "NONE", true, false, false, 1.0F));
	public static final SkyEffects COMMUNAL_CORRIDORS_SKY_EFFECTS = get(COMMUNAL_CORRIDORS, new StaticSkyEffects(Optional.empty(), false, "NONE", true, false, false, 1.0F));
	public static final SkyEffects HOARY_CROSSROADS_SKY_EFFECTS = get(HOARY_CROSSROADS, new StaticSkyEffects(Optional.empty(), false, "NONE", true, false, true, 1.0F));

	public static final PostEffect YEARNING_CANAL_POST_EFFECT = get(YEARNING_CANAL, new StaticPostEffect(TheCorners.id(YEARNING_CANAL)));
	public static final PostEffect COMMUNAL_CORRIDORS_POST_EFFECT = get(COMMUNAL_CORRIDORS, new StrongPostEffect(TheCorners.id(COMMUNAL_CORRIDORS), TheCorners.id(COMMUNAL_CORRIDORS + "_fallback")));
	public static final PostEffect HOARY_CROSSROADS_POST_EFFECT = get(HOARY_CROSSROADS, new StaticPostEffect(TheCorners.id(HOARY_CROSSROADS)));

	public static final LimlibWorld YEARNING_CANAL_WORLD = get(YEARNING_CANAL, new LimlibWorld(() -> new DimensionType(OptionalLong.of(1200), true, false, false, true, 1.0, true, false, 0, 2032, 2032, TagKey.of(Registry.BLOCK_KEY, TheCorners.id(YEARNING_CANAL)), TheCorners.id(YEARNING_CANAL), 1.0F, new MonsterSettings(false, false, ConstantIntProvider.ZERO, 0)), () -> new DimensionOptions(BuiltinRegistries.DIMENSION_TYPE.m_pselvvxn(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, TheCorners.id(YEARNING_CANAL))), new YearningCanalChunkGenerator(new FixedBiomeSource(BuiltinRegistries.BIOME.m_pselvvxn(CornerBiomes.YEARNING_CANAL_BIOME))))));
	public static final LimlibWorld COMMUNAL_CORRIDORS_WORLD = get(COMMUNAL_CORRIDORS, new LimlibWorld(() -> new DimensionType(OptionalLong.of(23500), true, false, false, true, 1.0, true, false, 0, 128, 128, TagKey.of(Registry.BLOCK_KEY, TheCorners.id(COMMUNAL_CORRIDORS)), TheCorners.id(COMMUNAL_CORRIDORS), 0.075F, new MonsterSettings(false, false, ConstantIntProvider.ZERO, 0)), () -> new DimensionOptions(BuiltinRegistries.DIMENSION_TYPE.m_pselvvxn(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, TheCorners.id(COMMUNAL_CORRIDORS))), new CommunalCorridorsChunkGenerator(new FixedBiomeSource(BuiltinRegistries.BIOME.m_pselvvxn(CornerBiomes.COMMUNAL_CORRIDORS_BIOME))))));
	public static final LimlibWorld HOARY_CROSSROADS_WORLD = get(HOARY_CROSSROADS, new LimlibWorld(() -> new DimensionType(OptionalLong.of(1200), true, false, false, true, 1.0, true, false, 0, 512, 512, TagKey.of(Registry.BLOCK_KEY, TheCorners.id(HOARY_CROSSROADS)), TheCorners.id(HOARY_CROSSROADS), 0.725F, new MonsterSettings(false, false, ConstantIntProvider.ZERO, 0)), () -> new DimensionOptions(BuiltinRegistries.DIMENSION_TYPE.m_pselvvxn(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, TheCorners.id(HOARY_CROSSROADS))), new HoaryCrossroadsChunkGenerator(new FixedBiomeSource(BuiltinRegistries.BIOME.m_pselvvxn(CornerBiomes.HOARY_CROSSROADS_BIOME)), 16, 16, 8, 0))));

	@Override
	public void register() {
	}

	public static <S extends SoundEffects> S get(String id, S soundEffects) {
		return Registry.register(SoundEffects.SOUND_EFFECTS, TheCorners.id(id), soundEffects);
	}

	public static <S extends Skybox> S get(String id, S skybox) {
		return Registry.register(Skybox.SKYBOX, TheCorners.id(id), skybox);
	}

	public static <S extends SkyEffects> S get(String id, S skyEffects) {
		return Registry.register(SkyEffects.SKY_EFFECTS, TheCorners.id(id), skyEffects);
	}

	public static <P extends PostEffect> P get(String id, P postEffect) {
		return Registry.register(PostEffect.POST_EFFECT, TheCorners.id(id), postEffect);
	}

	public static <W extends LimlibWorld> W get(String id, W world) {
		return Registry.register(LimlibWorld.LIMLIB_WORLD, TheCorners.id(id), world);
	}

}
