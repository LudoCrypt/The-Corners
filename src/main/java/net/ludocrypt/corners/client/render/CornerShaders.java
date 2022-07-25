package net.ludocrypt.corners.client.render;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.limlib.api.render.LiminalCoreShader;
import net.ludocrypt.limlib.api.render.LiminalQuadRenderer;
import net.ludocrypt.limlib.impl.LimlibRendering;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.registry.Registry;

public class CornerShaders {

	public static final LiminalCoreShader SKYBOX_SHADER = Registry.register(LimlibRendering.LIMINAL_CORE_SHADER, TheCorners.id("skybox"), new LiminalCoreShader(VertexFormats.POSITION));
	public static final LiminalQuadRenderer SKYBOX_QUAD_RENDERER = Registry.register(LimlibRendering.LIMINAL_QUAD_RENDERER, TheCorners.id("skybox"), new SkyboxQuadRenderer());

	public static void init() {

	}

}
