package net.ludocrypt.corners.client.entity.corvus;

import net.minecraft.client.render.animation.Animation;
import net.minecraft.client.render.animation.AnimationKeyframe;
import net.minecraft.client.render.animation.Animator;
import net.minecraft.client.render.animation.PartAnimation;

public class CorvusAnimations {

	public static final AnimationKeyframe ROTATE_ORIGIN = new AnimationKeyframe(0.0F, Animator.rotate(0.0F, 0.0F, 0.0F), PartAnimation.Interpolators.SPLINE);

	public static Animation.Builder tuckWings(Animation.Builder curr, AnimationKeyframe leftWing, AnimationKeyframe leftWing2, AnimationKeyframe leftWingMid, AnimationKeyframe rightWing,
			AnimationKeyframe rightWing2, AnimationKeyframe rightWingMid, float time) {
		return curr
				.addPartAnimation("left_wing",
						new PartAnimation(PartAnimation.AnimationTargets.ROTATE, leftWing, new AnimationKeyframe(time, Animator.rotate(0.0F, 0.0F, 90.0F), PartAnimation.Interpolators.SPLINE)))
				.addPartAnimation("left_wing_2",
						new PartAnimation(PartAnimation.AnimationTargets.ROTATE, leftWing2, new AnimationKeyframe(time, Animator.rotate(20.0F, -90.0F, -30.0F), PartAnimation.Interpolators.SPLINE)))
				.addPartAnimation("left_wing_mid_r1",
						new PartAnimation(PartAnimation.AnimationTargets.ROTATE, leftWingMid, new AnimationKeyframe(time, Animator.rotate(0.0F, 90.0F, 0.0F), PartAnimation.Interpolators.SPLINE)))
				.addPartAnimation("right_wing",
						new PartAnimation(PartAnimation.AnimationTargets.ROTATE, rightWing, new AnimationKeyframe(time, Animator.rotate(0.0F, 0.0F, -90.0F), PartAnimation.Interpolators.SPLINE)))
				.addPartAnimation("right_wing_2",
						new PartAnimation(PartAnimation.AnimationTargets.ROTATE, rightWing2, new AnimationKeyframe(time, Animator.rotate(20.0F, 90.0F, 30.0F), PartAnimation.Interpolators.SPLINE)))
				.addPartAnimation("right_wing_mid_r1",
						new PartAnimation(PartAnimation.AnimationTargets.ROTATE, rightWingMid, new AnimationKeyframe(time, Animator.rotate(0.0F, -90.0F, 0.0F), PartAnimation.Interpolators.SPLINE)));
	}

}
