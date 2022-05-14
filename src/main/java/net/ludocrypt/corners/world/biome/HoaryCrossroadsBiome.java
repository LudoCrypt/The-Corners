package net.ludocrypt.corners.world.biome;

import net.ludocrypt.corners.init.CornerSoundEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class HoaryCrossroadsBiome {

	public static Biome create() {
		Biome.Builder biome = new Biome.Builder();

		SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();

		GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();

		BiomeEffects.Builder biomeEffects = new BiomeEffects.Builder();
		biomeEffects.skyColor(16777215);
		biomeEffects.waterColor(16777215);
		biomeEffects.waterFogColor(16777215);
		biomeEffects.fogColor(16777215);
		biomeEffects.grassColor(12252383);
		biomeEffects.loopSound(CornerSoundEvents.BIOME_LOOP_HOARY_CROSSROADS);
		BiomeEffects effects = biomeEffects.build();

		biome.spawnSettings(spawnSettings.build());
		biome.generationSettings(generationSettings.build());
		biome.effects(effects);

		biome.precipitation(Biome.Precipitation.SNOW);
		biome.category(Biome.Category.NONE);

		biome.temperature(-1.0F);
		biome.downfall(1.0F);

		return biome.build();
	}

}
