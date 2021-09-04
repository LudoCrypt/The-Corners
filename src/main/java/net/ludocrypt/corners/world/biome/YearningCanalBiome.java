package net.ludocrypt.corners.world.biome;

import net.ludocrypt.corners.util.Color;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public class YearningCanalBiome {

	public static Biome create() {
		Biome.Builder biome = new Biome.Builder();

		SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();

		GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();
		generationSettings.surfaceBuilder(ConfiguredSurfaceBuilders.NOPE);

		BiomeEffects.Builder biomeEffects = new BiomeEffects.Builder();
		biomeEffects.skyColor(Color.of(255, 255, 255));
		biomeEffects.waterColor(Color.of(255, 255, 255));
		biomeEffects.waterFogColor(Color.of(255, 255, 255));
		biomeEffects.fogColor(Color.of(255, 255, 255));
		biomeEffects.grassColor(Color.of(226, 202, 132));
		BiomeEffects effects = biomeEffects.build();

		biome.spawnSettings(spawnSettings.build());
		biome.generationSettings(generationSettings.build());
		biome.effects(effects);

		biome.precipitation(Biome.Precipitation.NONE);
		biome.category(Biome.Category.NONE);

		biome.depth(0.0F);
		biome.scale(0.0F);

		biome.temperature(0.8F);
		biome.downfall(0.0F);

		return biome.build();
	}

}
