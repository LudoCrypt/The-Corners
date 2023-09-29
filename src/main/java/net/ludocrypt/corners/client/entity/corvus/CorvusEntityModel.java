package net.ludocrypt.corners.client.entity.corvus;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.entity.covrus.CorvusEntity;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class CorvusEntityModel<T extends CorvusEntity> extends SinglePartEntityModel<T> {
	public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(TheCorners.id("corvus"), "main");
	private final ModelPart main;

	public CorvusEntityModel(ModelPart root) {
		this.main = root.getChild("main");
	}

	public static TexturedModelData createBodyLayer() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData body = main.addChild(
				"body", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -5.0F, -6.0F, 6.0F, 5.0F, 8.0F, new Dilation(0.0F)).uv(27, 36)
						.cuboid(-3.0F, -5.5F, -6.5F, 6.0F, 6.0F, 8.0F, new Dilation(0.0F)).uv(16, 14).cuboid(-2.0F, -5.0F, 2.0F, 4.0F, 3.0F, 6.0F, new Dilation(0.0F)),
				ModelTransform.of(0.0F, -4.1F, -0.6F, -0.8727F, 0.0F, 0.0F));

		ModelPartData left_wing = body.addChild("left_wing", ModelPartBuilder.create(), ModelTransform.pivot(3.5F, -2.9F, -4.8F));

		ModelPartData cube_r1 = left_wing.addChild("cube_r1",
				ModelPartBuilder.create().uv(2, 28).cuboid(1.8F, -8.0F, -5.8F, 2.0F, 4.0F, 4.0F, new Dilation(0.0F)).uv(5, 32).cuboid(1.8F, -6.0F, -5.8F, 0.0F, 6.0F, 4.0F, new Dilation(0.0F)),
				ModelTransform.of(5.2F, -2.5F, 7.2F, 1.5708F, 0.0F, 1.5708F));

		ModelPartData left_wing_2 = left_wing.addChild("left_wing_2", ModelPartBuilder.create(), ModelTransform.pivot(3.4F, -0.7F, -0.8F));

		ModelPartData left_wing_end_r1 = left_wing_2.addChild("left_wing_end_r1", ModelPartBuilder.create().uv(2, 38).cuboid(0.0F, 0.0F, 0.0F, 0.0F, 6.0F, 9.0F, new Dilation(0.0F)),
				ModelTransform.of(2.0F, 0.4F, 0.0F, 1.5708F, 0.0F, 1.5708F));

		ModelPartData cube_r2 = left_wing_2.addChild("cube_r2", ModelPartBuilder.create().uv(26, 0).cuboid(1.8F, -8.0F, -7.8F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)),
				ModelTransform.of(7.8F, -1.8F, 8.0F, 1.5708F, 0.0F, 1.5708F));

		ModelPartData left_wing_mid_r1 = left_wing_2.addChild("left_wing_mid_r1", ModelPartBuilder.create().uv(8, 31).cuboid(0.0F, 0.0F, 0.0F, 0.0F, 6.0F, 5.0F, new Dilation(0.0F)),
				ModelTransform.of(0.0F, 0.0F, 2.0F, 1.5708F, 0.0F, 1.5708F));

		ModelPartData right_wing = body.addChild("right_wing", ModelPartBuilder.create(), ModelTransform.pivot(-3.5F, -2.9F, -4.8F));

		ModelPartData cube_r3 = right_wing.addChild("cube_r3", ModelPartBuilder.create().uv(2, 28).mirrored().cuboid(-3.8F, -8.0F, -5.8F, 2.0F, 4.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
				.uv(5, 32).mirrored().cuboid(-1.8F, -6.0F, -5.8F, 0.0F, 6.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-5.2F, -2.5F, 7.2F, 1.5708F, 0.0F, -1.5708F));

		ModelPartData right_wing_2 = right_wing.addChild("right_wing_2", ModelPartBuilder.create(), ModelTransform.pivot(-3.4F, -0.7F, -0.8F));

		ModelPartData right_wing_end_r1 = right_wing_2.addChild("right_wing_end_r1",
				ModelPartBuilder.create().uv(2, 38).mirrored().cuboid(0.0F, 0.0F, 0.0F, 0.0F, 6.0F, 9.0F, new Dilation(0.0F)).mirrored(false),
				ModelTransform.of(-2.0F, 0.4F, 0.0F, 1.5708F, 0.0F, -1.5708F));

		ModelPartData cube_r4 = right_wing_2.addChild("cube_r4", ModelPartBuilder.create().uv(26, 0).mirrored().cuboid(-2.8F, -8.0F, -7.8F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)).mirrored(false),
				ModelTransform.of(-7.8F, -1.8F, 8.0F, 1.5708F, 0.0F, -1.5708F));

		ModelPartData right_wing_mid_r1 = right_wing_2.addChild("right_wing_mid_r1",
				ModelPartBuilder.create().uv(8, 31).mirrored().cuboid(0.0F, 0.0F, 0.0F, 0.0F, 6.0F, 5.0F, new Dilation(0.0F)).mirrored(false),
				ModelTransform.of(0.0F, 0.0F, 2.0F, 1.5708F, 0.0F, -1.5708F));

		ModelPartData head_animation = body.addChild("head_animation", ModelPartBuilder.create(), ModelTransform.of(0.0F, -3.9F, -5.9F, -0.6545F, 0.0F, 0.0F));

		ModelPartData head = head_animation.addChild("head",
				ModelPartBuilder.create().uv(12, 23).cuboid(-2.0F, -1.7F, -3.2F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F)).uv(0, 0).cuboid(-1.0F, 3.3F, -2.6F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
				ModelTransform.pivot(0.0F, 0.9F, 0.6F));

		ModelPartData tail = body.addChild("tail", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -4.4F, 2.0F));

		ModelPartData right_tail = tail.addChild("right_tail", ModelPartBuilder.create().uv(14, 0).cuboid(-1.0F, 0.0F, 0.0F, 3.0F, 0.0F, 6.0F, new Dilation(0.0F)),
				ModelTransform.of(-1.0F, 0.0F, 0.0F, 0.0F, -0.4363F, 0.0F));

		ModelPartData left_tail = tail.addChild("left_tail", ModelPartBuilder.create().uv(20, 0).cuboid(-2.0F, 0.0F, 0.0F, 3.0F, 0.0F, 6.0F, new Dilation(0.0F)),
				ModelTransform.of(1.0F, 0.0F, 0.0F, 0.0F, 0.4363F, 0.0F));

		ModelPartData left_leg = main.addChild("left_leg",
				ModelPartBuilder.create().uv(48, 23).cuboid(-1.0F, -1.5F, -0.9F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(0, 25).cuboid(-0.5F, -0.5F, -0.4F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
				ModelTransform.of(1.7F, -2.5F, 0.2F, 0.0F, -0.2182F, 0.0F));

		ModelPartData cube_r5 = left_leg.addChild("cube_r5", ModelPartBuilder.create().uv(29, 27).cuboid(-1.5F, 0.5F, -1.2F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)),
				ModelTransform.of(0.0F, 1.5F, 0.1F, -0.6981F, 0.0F, 0.0F));

		ModelPartData right_leg = main.addChild("right_leg", ModelPartBuilder.create().uv(48, 23).mirrored().cuboid(-1.0F, -1.5F, -0.9F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).mirrored(false).uv(0, 25)
				.mirrored().cuboid(-0.5F, -0.5F, -0.4F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.7F, -2.5F, 0.2F, 0.0F, 0.2182F, 0.0F));

		ModelPartData cube_r6 = right_leg.addChild("cube_r6", ModelPartBuilder.create().uv(29, 27).mirrored().cuboid(-1.5F, 0.5F, -1.2F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)).mirrored(false),
				ModelTransform.of(0.0F, 1.5F, 0.1F, -0.6981F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(T corvus, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.main.traverse().forEach(ModelPart::resetTransform);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.main.render(matrices, vertices, light, overlay);
	}

	@Override
	public ModelPart getPart() {
		return this.main;
	}

}
