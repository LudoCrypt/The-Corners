package net.ludocrypt.corners.world.biome;

import net.ludocrypt.corners.init.CornerSoundEvents;
import net.minecraft.registry.HolderProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;

public class CommunalCorridorsBiome {

	public static Biome create(HolderProvider<PlacedFeature> features, HolderProvider<ConfiguredCarver<?>> carvers) {
		Biome.Builder biome = new Biome.Builder();
		SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
		GenerationSettings.Builder generationSettings = new GenerationSettings.Builder(features, carvers);
		BiomeEffects.Builder biomeEffects = new BiomeEffects.Builder();
		biomeEffects.skyColor(13548960);
		biomeEffects.waterColor(13548960);
		biomeEffects.waterFogColor(13548960);
		biomeEffects.fogColor(13548960);
		biomeEffects.grassColor(13818488);
		biomeEffects.loopSound(CornerSoundEvents.BIOME_LOOP_COMMUNAL_CORRIDORS);
		BiomeEffects effects = biomeEffects.build();
		biome.spawnSettings(spawnSettings.build());
		biome.generationSettings(generationSettings.build());
		biome.effects(effects);
		biome.hasPrecipitation(false);
		biome.temperature(0.8F);
		biome.downfall(0.0F);
		return biome.build();
	}

}
