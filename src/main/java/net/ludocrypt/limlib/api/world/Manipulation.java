package net.ludocrypt.limlib.api.world;

import com.mojang.serialization.Codec;

import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;

public enum Manipulation implements StringIdentifiable {

	NONE("none", BlockRotation.NONE, BlockMirror.NONE),
	CLOCKWISE_90("clockwise_90", BlockRotation.CLOCKWISE_90, BlockMirror.NONE),
	CLOCKWISE_180("180", BlockRotation.CLOCKWISE_180, BlockMirror.NONE),
	COUNTERCLOCKWISE_90("counterclockwise_90", BlockRotation.COUNTERCLOCKWISE_90, BlockMirror.NONE),
	FRONT_BACK("front_back", BlockRotation.NONE, BlockMirror.FRONT_BACK),
	LEFT_RIGHT("left_right", BlockRotation.NONE, BlockMirror.LEFT_RIGHT),
	TOP_LEFT_BOTTOM_RIGHT("top_left_bottom_right", BlockRotation.COUNTERCLOCKWISE_90, BlockMirror.LEFT_RIGHT),
	TOP_RIGHT_BOTTOM_LEFT("top_right_bottom_left", BlockRotation.CLOCKWISE_90, BlockMirror.LEFT_RIGHT);

	public static final Codec<Manipulation> CODEC = StringIdentifiable.createCodec(Manipulation::values);
	final String id;
	final BlockRotation rotation;
	final BlockMirror mirror;

	Manipulation(String id, BlockRotation rotation, BlockMirror mirror) {
		this.id = id;
		this.rotation = rotation;
		this.mirror = mirror;
	}

	public BlockRotation getRotation() {
		return rotation;
	}

	public BlockMirror getMirror() {
		return mirror;
	}

	@Override
	public String asString() {
		return id;
	}

	public static Manipulation random(RandomGenerator random) {
		return Manipulation.values()[random.nextInt(8)];
	}

	public static Manipulation of(BlockRotation rotation) {
		return of(rotation, BlockMirror.NONE);
	}

	public static Manipulation of(BlockMirror mirror) {
		return of(BlockRotation.NONE, mirror);
	}

	public static Manipulation of(Direction from, Direction to) {
		return switch (from) {
			case DOWN -> throw new UnsupportedOperationException();
			case UP -> throw new UnsupportedOperationException();
			case EAST -> (switch (to) {
				case DOWN -> throw new UnsupportedOperationException();
				case UP -> throw new UnsupportedOperationException();
				case EAST -> NONE;
				case NORTH -> COUNTERCLOCKWISE_90;
				case SOUTH -> CLOCKWISE_90;
				case WEST -> CLOCKWISE_180;
			});
			case NORTH -> (switch (to) {
				case DOWN -> throw new UnsupportedOperationException();
				case UP -> throw new UnsupportedOperationException();
				case EAST -> CLOCKWISE_90;
				case NORTH -> NONE;
				case SOUTH -> CLOCKWISE_180;
				case WEST -> COUNTERCLOCKWISE_90;
			});
			case SOUTH -> (switch (to) {
				case DOWN -> throw new UnsupportedOperationException();
				case UP -> throw new UnsupportedOperationException();
				case EAST -> COUNTERCLOCKWISE_90;
				case NORTH -> CLOCKWISE_180;
				case SOUTH -> NONE;
				case WEST -> CLOCKWISE_90;
			});
			case WEST -> (switch (to) {
				case DOWN -> throw new UnsupportedOperationException();
				case UP -> throw new UnsupportedOperationException();
				case EAST -> CLOCKWISE_180;
				case NORTH -> CLOCKWISE_90;
				case SOUTH -> COUNTERCLOCKWISE_90;
				case WEST -> NONE;
			});
		};
	}

	public static Manipulation of(BlockRotation rotation, BlockMirror mirror) {
		return switch (rotation) {
			case NONE -> (switch (mirror) {
				case NONE -> NONE;
				case FRONT_BACK -> FRONT_BACK;
				case LEFT_RIGHT -> LEFT_RIGHT;
			});
			case CLOCKWISE_180 -> (switch (mirror) {
				case NONE -> CLOCKWISE_180;
				case FRONT_BACK -> LEFT_RIGHT;
				case LEFT_RIGHT -> FRONT_BACK;
			});
			case CLOCKWISE_90 -> (switch (mirror) {
				case NONE -> CLOCKWISE_90;
				case FRONT_BACK -> TOP_LEFT_BOTTOM_RIGHT;
				case LEFT_RIGHT -> TOP_RIGHT_BOTTOM_LEFT;
			});
			case COUNTERCLOCKWISE_90 -> (switch (mirror) {
				case NONE -> COUNTERCLOCKWISE_90;
				case FRONT_BACK -> TOP_RIGHT_BOTTOM_LEFT;
				case LEFT_RIGHT -> TOP_LEFT_BOTTOM_RIGHT;
			});
		};
	}

	public Manipulation rotate(BlockRotation rotation) {
		return switch (rotation) {
			case NONE -> this;
			case CLOCKWISE_180 -> (switch (this) {
				case NONE -> CLOCKWISE_180;
				case FRONT_BACK -> LEFT_RIGHT;
				case LEFT_RIGHT -> FRONT_BACK;
				case CLOCKWISE_180 -> NONE;
				case CLOCKWISE_90 -> COUNTERCLOCKWISE_90;
				case COUNTERCLOCKWISE_90 -> CLOCKWISE_90;
				case TOP_LEFT_BOTTOM_RIGHT -> TOP_RIGHT_BOTTOM_LEFT;
				case TOP_RIGHT_BOTTOM_LEFT -> TOP_LEFT_BOTTOM_RIGHT;
			});
			case CLOCKWISE_90 -> (switch (this) {
				case NONE -> CLOCKWISE_90;
				case FRONT_BACK -> TOP_RIGHT_BOTTOM_LEFT;
				case LEFT_RIGHT -> TOP_LEFT_BOTTOM_RIGHT;
				case CLOCKWISE_180 -> COUNTERCLOCKWISE_90;
				case CLOCKWISE_90 -> CLOCKWISE_180;
				case COUNTERCLOCKWISE_90 -> NONE;
				case TOP_LEFT_BOTTOM_RIGHT -> FRONT_BACK;
				case TOP_RIGHT_BOTTOM_LEFT -> LEFT_RIGHT;
			});
			case COUNTERCLOCKWISE_90 -> (switch (this) {
				case NONE -> COUNTERCLOCKWISE_90;
				case FRONT_BACK -> TOP_LEFT_BOTTOM_RIGHT;
				case LEFT_RIGHT -> TOP_RIGHT_BOTTOM_LEFT;
				case CLOCKWISE_180 -> CLOCKWISE_90;
				case CLOCKWISE_90 -> NONE;
				case COUNTERCLOCKWISE_90 -> CLOCKWISE_180;
				case TOP_LEFT_BOTTOM_RIGHT -> LEFT_RIGHT;
				case TOP_RIGHT_BOTTOM_LEFT -> FRONT_BACK;
			});
		};
	}

	public Manipulation mirror(BlockMirror mirror) {
		return switch (mirror) {
			case NONE -> this;
			case FRONT_BACK -> (switch (this) {
				case NONE -> FRONT_BACK;
				case FRONT_BACK -> NONE;
				case LEFT_RIGHT -> CLOCKWISE_180;
				case CLOCKWISE_180 -> LEFT_RIGHT;
				case CLOCKWISE_90 -> TOP_LEFT_BOTTOM_RIGHT;
				case COUNTERCLOCKWISE_90 -> TOP_RIGHT_BOTTOM_LEFT;
				case TOP_LEFT_BOTTOM_RIGHT -> CLOCKWISE_90;
				case TOP_RIGHT_BOTTOM_LEFT -> COUNTERCLOCKWISE_90;
			});
			case LEFT_RIGHT -> (switch (this) {
				case NONE -> LEFT_RIGHT;
				case FRONT_BACK -> CLOCKWISE_180;
				case LEFT_RIGHT -> NONE;
				case CLOCKWISE_180 -> FRONT_BACK;
				case CLOCKWISE_90 -> TOP_RIGHT_BOTTOM_LEFT;
				case COUNTERCLOCKWISE_90 -> TOP_LEFT_BOTTOM_RIGHT;
				case TOP_LEFT_BOTTOM_RIGHT -> COUNTERCLOCKWISE_90;
				case TOP_RIGHT_BOTTOM_LEFT -> CLOCKWISE_90;
			});
		};
	}

	public Manipulation manipulate(Manipulation manipulation) {
		return this.rotate(manipulation.rotation).mirror(manipulation.mirror);
	}

	public BlockRotation flattenRotation() {
		return switch (this) {
			case NONE -> BlockRotation.NONE;
			case FRONT_BACK -> BlockRotation.CLOCKWISE_180;
			case LEFT_RIGHT -> BlockRotation.NONE;
			case CLOCKWISE_180 -> BlockRotation.CLOCKWISE_180;
			case CLOCKWISE_90 -> BlockRotation.CLOCKWISE_90;
			case COUNTERCLOCKWISE_90 -> BlockRotation.COUNTERCLOCKWISE_90;
			case TOP_LEFT_BOTTOM_RIGHT -> BlockRotation.CLOCKWISE_90;
			case TOP_RIGHT_BOTTOM_LEFT -> BlockRotation.COUNTERCLOCKWISE_90;
		};
	}

	public static Manipulation[] rotations() {
		return new Manipulation[] { Manipulation.NONE, Manipulation.CLOCKWISE_90, Manipulation.CLOCKWISE_180,
			Manipulation.COUNTERCLOCKWISE_90 };
	}

	public static Manipulation[] mirrors() {
		return new Manipulation[] { Manipulation.NONE, Manipulation.FRONT_BACK, Manipulation.LEFT_RIGHT,
			Manipulation.TOP_LEFT_BOTTOM_RIGHT, Manipulation.TOP_RIGHT_BOTTOM_LEFT };
	}

}
