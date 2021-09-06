package net.ludocrypt.corners.client.sound;

public interface UnnaturalStopper {

	public boolean isStoppingUnnaturally();

	public void forceStoppingUnnaturally(boolean unnatural);

	public default boolean isStoppingNaturally() {
		return !isStoppingUnnaturally();
	}

	public default void setStoppingUnnaturally() {
		forceStoppingUnnaturally(true);
	}

	public default void setStoppingNaturally() {
		forceStoppingUnnaturally(false);
	}

}
