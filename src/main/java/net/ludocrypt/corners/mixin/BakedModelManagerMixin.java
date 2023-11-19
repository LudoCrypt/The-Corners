package net.ludocrypt.corners.mixin;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.client.render.DeepBookshelfRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.util.Identifier;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin {

	@Shadow
	@Final
	@Mutable
	private static Map<Identifier, Identifier> ATLAS_RESOURCES;
	static {
		Map<Identifier, Identifier> newAtli = new HashMap<Identifier, Identifier>();
		newAtli.putAll(ATLAS_RESOURCES);
		newAtli.put(DeepBookshelfRenderer.DEEP_BOOKSHELF_ATLAS_TEXTURE, TheCorners.id("deep"));
		ATLAS_RESOURCES = Map.copyOf(newAtli);
	}

}
