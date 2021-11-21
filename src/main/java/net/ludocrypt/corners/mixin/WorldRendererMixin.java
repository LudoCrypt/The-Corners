package net.ludocrypt.corners.mixin;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.access.BlockRenderManagerAccess;
import net.ludocrypt.corners.access.ContainsSkyboxBlocksAccess;
import net.ludocrypt.corners.access.SodiumWorldRendererAccess;
import net.ludocrypt.corners.client.TheCornersClient;
import net.ludocrypt.corners.client.render.sky.SkyboxShaders;
import net.ludocrypt.corners.init.CornerSoundEvents;
import net.ludocrypt.corners.init.CornerWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
@Mixin(value = WorldRenderer.class, priority = 1001)
public class WorldRendererMixin {

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	@Final
	private TextureManager textureManager;

	@Shadow
	private ClientWorld world;

	@Shadow
	@Final
	private ObjectArrayList<WorldRenderer.ChunkInfo> visibleChunks;

	@Shadow
	@Final
	private BufferBuilderStorage bufferBuilders;

	@Inject(method = "processWorldEvent", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void corners$processWorldEvent(PlayerEntity source, int eventId, BlockPos pos, int data, CallbackInfo ci, Random random) {
		switch (eventId) {
		case 29848748:
			this.client.getSoundManager().play(PositionedSoundInstance.ambient(CornerSoundEvents.PAINTING_PORTAL_TRAVEL, random.nextFloat() * 0.4F + 0.8F, 0.25F));
			break;
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V", shift = At.Shift.AFTER, remap = false))
	private void corners$render$clear(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		if (!FabricLoader.getInstance().isModLoaded("iris")) {
			this.corners$render$skyboxes(matrices, camera);
		}
	}

	@Inject(method = "render", at = @At(value = "RETURN", shift = Shift.BEFORE))
	private void corners$render$return(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		if (FabricLoader.getInstance().isModLoaded("iris")) {
			this.corners$render$skyboxes(matrices, camera);
		}
	}

	@Unique
	private void corners$render$skyboxes(MatrixStack matrices, Camera camera) {
		// Render Skyboxes
		if (this.world.getRegistryKey().equals(CornerWorld.YEARNING_CANAL_WORLD_REGISTRY_KEY)) {
			this.corners$renderCubemap(matrices, TheCorners.id("textures/sky/yearning_canal"));
		} else if (this.world.getRegistryKey().equals(CornerWorld.COMMUNAL_CORRIDORS_WORLD_REGISTRY_KEY)) {
			this.corners$renderCubemap(matrices, TheCorners.id("textures/sky/snow"));
		}

		// Render Skybox Quads
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.depthMask(true);
		RenderSystem.polygonOffset(3.0F, 3.0F);
		RenderSystem.enablePolygonOffset();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

		MatrixStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.push();
		modelViewStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
		modelViewStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw()));
		modelViewStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
		RenderSystem.applyModelViewMatrix();

		this.getSkyboxPairs().forEach((pair) -> {
			BlockPos pos = pair.getFirst();
			BlockState state = pair.getSecond();
			matrices.push();
			matrices.translate(pos.getX() - camera.getPos().getX(), pos.getY() - camera.getPos().getY(), pos.getZ() - camera.getPos().getZ());

			List<BakedQuad> quads = Lists.newArrayList();
			BakedModel model = ((BlockRenderManagerAccess) MinecraftClient.getInstance().getBlockRenderManager()).getModelPure(state);
			SkyboxShaders.addAll(quads, model, state, new Random(state.getRenderingSeed(pos)));

			for (Direction dir : Direction.values()) {
				if (Block.shouldDrawSide(state, world, pos, dir, pos.offset(dir))) {
					SkyboxShaders.addAll(quads, model, state, dir, new Random(state.getRenderingSeed(pos)));
				}
			}

			Iterator<BakedQuad> quadIterator = quads.iterator();

			while (quadIterator.hasNext()) {
				BakedQuad quad = quadIterator.next();
				RenderSystem.setShader(() -> SkyboxShaders.SKYBOX_SHADER);
				for (int i = 0; i < 6; i++) {
					RenderSystem.setShaderTexture(i, new Identifier(quad.getSprite().getId().getNamespace(), "textures/" + quad.getSprite().getId().getPath() + "_" + i + ".png"));
				}

				Matrix4f matrix = matrices.peek().getModel().copy();
				matrix.loadIdentity();
				matrix.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(180));
				matrix.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(camera.getYaw()));
				matrix.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(camera.getPitch()));
				matrix.multiply(matrices.peek().getModel().copy());

				SkyboxShaders.quad((vec3f) -> bufferBuilder.vertex(vec3f.getX(), vec3f.getY(), vec3f.getZ()).next(), matrix, quad);
			}
			matrices.pop();
		});

		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.polygonOffset(0.0F, 0.0F);
		RenderSystem.disablePolygonOffset();
		modelViewStack.pop();
		RenderSystem.applyModelViewMatrix();
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
	}

	@Unique
	private List<Pair<BlockPos, BlockState>> getSkyboxPairs() {
		if (!FabricLoader.getInstance().isModLoaded("sodium")) {
			List<Pair<BlockPos, BlockState>> list = Lists.newArrayList();
			this.visibleChunks.forEach((info) -> ((ContainsSkyboxBlocksAccess) ((WorldRendererChunkInfoAccessor) info).getChunk().data.get()).getSkyboxBlocks().forEach((pos, state) -> list.add(Pair.of(pos, state))));
			return list;
		} else {
			return ((SodiumWorldRendererAccess) (WorldRenderer) (Object) this).getSodiumSkyboxModelPairs();
		}
	}

	@Unique
	private void corners$renderCubemap(MatrixStack matrices, Identifier identifier) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());

		MinecraftClient client = MinecraftClient.getInstance();
		Vec3d color = client.world.method_23777(client.gameRenderer.getCamera().getPos(), TheCornersClient.getTickDelta()).multiply(255);
		int r = (int) Math.floor(color.x);
		int g = (int) Math.floor(color.y);
		int b = (int) Math.floor(color.z);
		int a = 255;
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

		for (int i = 0; i < 6; ++i) {
			matrices.push();
			if (i == 0) {
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
			}

			if (i == 1) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
			}

			if (i == 2) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
			}

			if (i == 3) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
			}

			if (i == 4) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
			}

			Matrix4f matrix4f = matrices.peek().getModel();

			RenderSystem.setShaderTexture(0, new Identifier(identifier.toString() + "_" + i + ".png"));
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(r, g, b, a).next();
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 1.0F).color(r, g, b, a).next();
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(1.0F, 1.0F).color(r, g, b, a).next();
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(1.0F, 0.0F).color(r, g, b, a).next();
			bufferBuilder.end();
			BufferRenderer.draw(bufferBuilder);
			matrices.pop();
		}

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

}
