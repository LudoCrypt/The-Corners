package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.corners.client.render.sky.YearningCanalSky;
import net.ludocrypt.corners.init.CornerWorld;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(SkyProperties.class)
public class SkyPropertiesMixin {

	@Shadow
	@Final
	private static Object2ObjectMap<Identifier, SkyProperties> BY_IDENTIFIER;

	static {
		BY_IDENTIFIER.put(CornerWorld.YEARNING_CANAL_ID, new YearningCanalSky());
	}

}
