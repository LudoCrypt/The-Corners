package net.ludocrypt.corners.entity.covrus.goal;

import java.util.EnumSet;

import net.ludocrypt.corners.entity.covrus.CorvusEntity;
import net.minecraft.entity.ai.goal.Goal;

public class CorvusIdlingGoal extends Goal {
	private final CorvusEntity mob;
	private double deltaX;
	private double deltaZ;
	private int lookTime;

	public CorvusIdlingGoal(CorvusEntity mob) {
		this.mob = mob;
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
	}

	@Override
	public boolean canStart() {
		return this.mob.getRandom().nextFloat() < 0.02F;
	}

	@Override
	public boolean shouldContinue() {
		return this.lookTime >= 0;
	}

	@Override
	public void start() {
		double d = Math.PI * 2 * this.mob.getRandom().nextDouble();
		this.deltaX = Math.cos(d);
		this.deltaZ = Math.sin(d);
		this.lookTime = 20 + this.mob.getRandom().nextInt(20);
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		--this.lookTime;
		this.mob.getLookControl().lookAt(this.mob.getX() + this.deltaX, this.mob.getEyeY(), this.mob.getZ() + this.deltaZ);
	}
}
