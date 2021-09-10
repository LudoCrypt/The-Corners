package net.ludocrypt.corners.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.init.CornerSoundEvents;
import net.ludocrypt.corners.init.CornerWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	@Final
	private TextureManager textureManager;

	@Shadow
	private ClientWorld world;

	@Inject(method = "processWorldEvent", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void corners$processWorldEvent(PlayerEntity source, int eventId, BlockPos pos, int data, CallbackInfo ci, Random random) {
		switch (eventId) {
		case 29848748:
			this.client.getSoundManager().play(PositionedSoundInstance.ambient(CornerSoundEvents.PAINTING_PORTAL_TRAVEL, random.nextFloat() * 0.4F + 0.8F, 0.25F));
			break;
		}
	}

	@Inject(method = "renderSky", at = @At("HEAD"))
	private void corners$renderSky(MatrixStack matrices, Matrix4f matrix4f, float f, Runnable runnable, CallbackInfo ci) {
		if (this.world.getRegistryKey().equals(CornerWorld.YEARNING_CANAL_WORLD_REGISTRY_KEY)) {
			this.corners$renderCubemap(matrices, TheCorners.id("textures/sky/yearning_canal"), 50, 255, 255, 255, f);
		}
	}

	@Unique
	private void corners$renderCubemap(MatrixStack matrices, Identifier identifier, int alpha, int r, int g, int b, float tickDelta) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);

		MinecraftClient client = MinecraftClient.getInstance();
		Vec3d color = client.world.method_23777(client.gameRenderer.getCamera().getPos(), tickDelta).multiply(255);
		int skyR = (int) Math.floor(color.x);
		int skyG = (int) Math.floor(color.y);
		int skyB = (int) Math.floor(color.z);
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

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

			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(matrix4f, -105.0F, -105.0F, -105.0F).color(skyR, skyG, skyB, 255).next();
			bufferBuilder.vertex(matrix4f, -105.0F, -105.0F, 105.0F).color(skyR, skyG, skyB, 255).next();
			bufferBuilder.vertex(matrix4f, 105.0F, -105.0F, 105.0F).color(skyR, skyG, skyB, 255).next();
			bufferBuilder.vertex(matrix4f, 105.0F, -105.0F, -105.0F).color(skyR, skyG, skyB, 255).next();
			tessellator.draw();
			matrices.pop();
		}

		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		tessellator = Tessellator.getInstance();
		bufferBuilder = tessellator.getBuffer();

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
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(r, g, b, alpha).next();
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 1.0F).color(r, g, b, alpha).next();
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(1.0F, 1.0F).color(r, g, b, alpha).next();
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(1.0F, 0.0F).color(r, g, b, alpha).next();
			tessellator.draw();
			matrices.pop();
		}

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

}
