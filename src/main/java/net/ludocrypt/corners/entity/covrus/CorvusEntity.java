package net.ludocrypt.corners.entity.covrus;

import net.ludocrypt.corners.entity.covrus.goal.CorvusIdlingGoal;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class CorvusEntity extends MobEntity implements Flutterer {

	public static final TrackedDataHandler<CorvusPose> CORVUS_POSE_DATA_HANDLER = TrackedDataHandler.createEnum(CorvusPose.class);
	public static final TrackedData<CorvusPose> CORVUS_POSE = DataTracker.registerData(CorvusEntity.class, CORVUS_POSE_DATA_HANDLER);
	public AnimationState restingAnimation = new AnimationState();

	public CorvusEntity(EntityType<? extends CorvusEntity> entityType, World world) {
		super(entityType, world);
		this.goalSelector.add(10, new CorvusIdlingGoal(this));
	}

	public static DefaultAttributeContainer.Builder createAttributes() {
		return MobEntity.createAttributes();
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CORVUS_POSE, CorvusPose.SITTING);
	}

	@Override
	public boolean isInAir() {
		return false;
	}

	public CorvusPose getCorvusPose() {
		return CorvusPose.IDLING;
	}

	@Override
	public void tick() {
		super.tick();

		if (this.age % 60 == 0) {
			this.restingAnimation.restart(this.age);
		}

	}

	static {
		TrackedDataHandlerRegistry.register(CORVUS_POSE_DATA_HANDLER);
	}

}
