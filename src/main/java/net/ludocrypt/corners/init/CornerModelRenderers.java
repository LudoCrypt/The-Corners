package net.ludocrypt.corners.init;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.client.render.SkyboxRenderer;
import net.ludocrypt.limlib.render.special.SpecialModelRenderer;
import net.minecraft.util.registry.Registry;

public class CornerModelRenderers {

	public static final SpecialModelRenderer SNOWY_SKYBOX_RENDERER = get("snowy_skybox", new SkyboxRenderer("snow"));
	public static final SpecialModelRenderer OFFICE_SKYBOX_RENDERER = get("office_skybox", new SkyboxRenderer("office"));
	public static final SpecialModelRenderer SUNBEACH_SKYBOX_RENDERER = get("sunbeach_skybox", new SkyboxRenderer("sunbeach"));

	public static void init() {

	}

	public static <S extends SpecialModelRenderer> S get(String id, S modelRenderer) {
		return Registry.register(SpecialModelRenderer.SPECIAL_MODEL_RENDERER, TheCorners.id(id), modelRenderer);
	}

}
