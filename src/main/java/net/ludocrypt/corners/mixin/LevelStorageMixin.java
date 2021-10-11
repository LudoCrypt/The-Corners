package net.ludocrypt.corners.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;

import net.minecraft.world.level.storage.LevelStorage;

@Mixin(LevelStorage.class)
public class LevelStorageMixin {

	@ModifyVariable(method = "readGeneratorProperties", at = @At(value = "STORE"), ordinal = 1)
	private static <T> Dynamic<T> corners$readGeneratorProperties$datafix(Dynamic<T> in, Dynamic<T> levelData, DataFixer dataFixer, int version) {
		Dynamic<T> dynamic = in;
		dynamic = addDimension("corners:yearning_canal", in);
		dynamic = addDimension("corners:communal_corridors", in);
		return dynamic;
	}

	@Unique
	private static <T> Dynamic<T> addDimension(String id, Dynamic<T> in) {
		if (in.get("dimensions").get(id).get().result().isEmpty()) {
			Map<Dynamic<T>, Dynamic<T>> map = Maps.newHashMap(in.getMapValues().result().get());
			Dynamic<T> dimensionsKey = in.createString("dimensions");
			Dynamic<T> dimensions = map.get(dimensionsKey);
			Map<Dynamic<T>, Dynamic<T>> dimensionsMap = Maps.newHashMap(dimensions.getMapValues().result().get());
			Dynamic<T> dimensionDynamic = new Dynamic<T>(dimensions.getOps());
			Map<Dynamic<T>, Dynamic<T>> dimensionMap = Maps.newHashMap();
			Map<Dynamic<T>, Dynamic<T>> generatorMap = Maps.newHashMap();
			Map<Dynamic<T>, Dynamic<T>> biomeSourceMap = Maps.newHashMap();
			biomeSourceMap.put(dimensionDynamic.createString("biome"), dimensionDynamic.createString(id));
			biomeSourceMap.put(dimensionDynamic.createString("type"), dimensionDynamic.createString("minecraft:fixed"));
			generatorMap.put(dimensionDynamic.createString("biome_source"), dimensionDynamic.createMap(biomeSourceMap));
			generatorMap.put(dimensionDynamic.createString("seed"), dimensionDynamic.createLong(in.get("seed").asLong(0)));
			generatorMap.put(dimensionDynamic.createString("type"), dimensionDynamic.createString(id + "_chunk_generator"));
			dimensionMap.put(dimensionDynamic.createString("generator"), dimensionDynamic.createMap(generatorMap));
			dimensionMap.put(dimensionDynamic.createString("type"), dimensionDynamic.createString(id));
			dimensionDynamic = dimensionDynamic.createMap(dimensionMap);
			dimensionsMap.put(dimensions.createString(id), dimensionDynamic);
			dimensions = dimensions.createMap(dimensionsMap);
			map.replace(dimensionsKey, dimensions);
			return in.createMap(map);
		}
		return in;
	}

}
