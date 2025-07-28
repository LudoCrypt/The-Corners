package net.ludocrypt.limlib.api.world.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.logging.log4j.util.TriConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.datafixers.util.Pair;

import net.ludocrypt.limlib.api.world.Manipulation;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ChunkRegion;

public class NbtPlacerUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(NbtPlacerUtil.class);

	public final NbtCompound storedNbt;
	public final HashMap<BlockPos, Pair<BlockState, Optional<NbtCompound>>> positions;
	public final NbtList entities;
	public final BlockPos lowestPos;
	public final int sizeX;
	public final int sizeY;
	public final int sizeZ;
	public final Vec3i sizeVector;

	public NbtPlacerUtil(NbtCompound storedNbt, HashMap<BlockPos, Pair<BlockState, Optional<NbtCompound>>> positions,
			NbtList entities, BlockPos lowestPos, int sizeX, int sizeY, int sizeZ) {
		this.storedNbt = storedNbt;
		this.positions = positions;
		this.entities = entities;
		this.lowestPos = lowestPos;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.sizeVector = new Vec3i(sizeX, sizeY, sizeZ);
	}

	public NbtPlacerUtil(NbtCompound storedNbt, HashMap<BlockPos, Pair<BlockState, Optional<NbtCompound>>> positions,
			NbtList entities, BlockPos lowestPos, BlockPos sizePos) {
		this(storedNbt, positions, entities, lowestPos, sizePos.getX(), sizePos.getY(), sizePos.getZ());
	}

	public NbtPlacerUtil manipulate(Manipulation manipulation) {
		NbtList paletteList = storedNbt.getList("palette", 10);
		HashMap<Integer, BlockState> palette = new HashMap<Integer, BlockState>(paletteList.size());
		List<NbtCompound> paletteCompoundList = paletteList
			.stream()
			.filter(nbtElement -> nbtElement instanceof NbtCompound)
			.map(element -> (NbtCompound) element)
			.toList();

		for (int i = 0; i < paletteCompoundList.size(); i++) {
			palette
				.put(i,
					NbtHelper
						.toBlockState(Registries.BLOCK.asLookup(), paletteCompoundList.get(i))
						.rotate(manipulation.getRotation())
						.mirror(manipulation.getMirror()));
		}

		NbtList sizeList = storedNbt.getList("size", 3);
		BlockPos sizeVectorRotated = NbtPlacerUtil
			.mirror(
				new BlockPos(sizeList.getInt(0), sizeList.getInt(1), sizeList.getInt(2)).rotate(manipulation.getRotation()),
				manipulation.getMirror());
		BlockPos sizeVector = new BlockPos(Math.abs(sizeVectorRotated.getX()), Math.abs(sizeVectorRotated.getY()),
			Math.abs(sizeVectorRotated.getZ()));
		NbtList positionsList = storedNbt.getList("blocks", 10);
		HashMap<BlockPos, Pair<BlockState, Optional<NbtCompound>>> positions = new HashMap<BlockPos, Pair<BlockState, Optional<NbtCompound>>>(
			positionsList.size());
		List<Pair<BlockPos, Pair<BlockState, Optional<NbtCompound>>>> positionsPairList = positionsList
			.stream()
			.filter(nbtElement -> nbtElement instanceof NbtCompound)
			.map(element -> (NbtCompound) element)
			.map((nbtCompound) -> Pair
				.of(NbtPlacerUtil
					.mirror(
						new BlockPos(nbtCompound.getList("pos", 3).getInt(0), nbtCompound.getList("pos", 3).getInt(1),
							nbtCompound.getList("pos", 3).getInt(2)).rotate(manipulation.getRotation()),
						manipulation.getMirror()),
					Pair
						.of(palette.get(nbtCompound.getInt("state")),
							nbtCompound.contains("nbt", NbtElement.COMPOUND_TYPE)
									? Optional.of(nbtCompound.getCompound("nbt"))
									: emptyNbt())))
			.sorted(Comparator.comparing((pair) -> pair.getFirst().getX()))
			.sorted(Comparator.comparing((pair) -> pair.getFirst().getY()))
			.sorted(Comparator.comparing((pair) -> pair.getFirst().getZ()))
			.toList();
		positionsPairList
			.forEach(
				(pair) -> positions.put(pair.getFirst().subtract(positionsPairList.get(0).getFirst()), pair.getSecond()));
		return new NbtPlacerUtil(storedNbt, positions, storedNbt.getList("entities", 10),
			transformSize(sizeVector, manipulation.getRotation(), manipulation.getMirror()), sizeVector);
	}

	public static NbtPlacerUtil load(Identifier id, ResourceManager manager) {
		return loadSafe(id, manager).get();
	}

	public static Optional<NbtPlacerUtil> loadSafe(Identifier id, ResourceManager manager) {

		Optional<NbtCompound> nbtOptional = loadNbtSafe(id, manager);

		if (nbtOptional.isPresent()) {
			NbtCompound nbt = nbtOptional.get();
			NbtList paletteList = nbt.getList("palette", 10);
			HashMap<Integer, BlockState> palette = new HashMap<Integer, BlockState>(paletteList.size());
			List<NbtCompound> paletteCompoundList = paletteList
				.stream()
				.filter(nbtElement -> nbtElement instanceof NbtCompound)
				.map(element -> (NbtCompound) element)
				.toList();

			for (int i = 0; i < paletteCompoundList.size(); i++) {
				palette.put(i, NbtHelper.toBlockState(Registries.BLOCK.asLookup(), paletteCompoundList.get(i)));
			}

			NbtList sizeList = nbt.getList("size", 3);
			BlockPos sizeVectorRotated = new BlockPos(sizeList.getInt(0), sizeList.getInt(1), sizeList.getInt(2));
			BlockPos sizeVector = new BlockPos(Math.abs(sizeVectorRotated.getX()), Math.abs(sizeVectorRotated.getY()),
				Math.abs(sizeVectorRotated.getZ()));
			NbtList positionsList = nbt.getList("blocks", 10);
			HashMap<BlockPos, Pair<BlockState, Optional<NbtCompound>>> positions = new HashMap<BlockPos, Pair<BlockState, Optional<NbtCompound>>>(
				positionsList.size());
			List<Pair<BlockPos, Pair<BlockState, Optional<NbtCompound>>>> positionsPairList = positionsList
				.stream()
				.filter(nbtElement -> nbtElement instanceof NbtCompound)
				.map(element -> (NbtCompound) element)
				.map((nbtCompound) -> Pair
					.of(new BlockPos(nbtCompound.getList("pos", 3).getInt(0), nbtCompound.getList("pos", 3).getInt(1),
						nbtCompound.getList("pos", 3).getInt(2)),
						Pair
							.of(palette.get(nbtCompound.getInt("state")),
								nbtCompound.contains("nbt", NbtElement.COMPOUND_TYPE)
										? Optional.of(nbtCompound.getCompound("nbt"))
										: emptyNbt())))
				.sorted(Comparator.comparing((pair) -> pair.getFirst().getX()))
				.sorted(Comparator.comparing((pair) -> pair.getFirst().getY()))
				.sorted(Comparator.comparing((pair) -> pair.getFirst().getZ()))
				.toList();
			positionsPairList
				.forEach((pair) -> positions
					.put(pair.getFirst().subtract(positionsPairList.get(0).getFirst()), pair.getSecond()));
			return Optional
				.of(new NbtPlacerUtil(nbt, positions, nbt.getList("entities", 10), positionsPairList.get(0).getFirst(),
					sizeVector));
		}

		return Optional.empty();
	}

	public static NbtCompound loadTags(Identifier id, ResourceManager manager) {
		return loadNbtSafe(id, manager).orElseGet(NbtCompound::new).getCompound("limlib_tag");
	}

	private static Optional<NbtCompound> emptyNbt() {
		return Optional.empty();
	}

	public static Optional<NbtCompound> loadNbtSafe(Identifier id, ResourceManager manager) {

		try {
			return Optional.ofNullable(readStructure(manager.getResource(id).get()));
		} catch (Exception e) {
			LOGGER.error("Failed to load structure '{}' {}", id, e);
			return Optional.empty();
		}

	}

	public static NbtCompound readStructure(Resource resource) throws IOException {
		InputStream stream = resource.open();
		NbtCompound nbt = NbtIo.readCompressed(stream);
		stream.close();
		return nbt;
	}

	public NbtPlacerUtil generateNbt(ChunkRegion region, BlockPos at,
			TriConsumer<BlockPos, BlockState, Optional<NbtCompound>> consumer) {
		return generateNbt(region, BlockPos.ZERO, at, new Bound(at.add(this.sizeVector)), consumer);
	}

	public NbtPlacerUtil generateNbt(ChunkRegion region, Vec3i offset, BlockPos from, Bound bound,
			TriConsumer<BlockPos, BlockState, Optional<NbtCompound>> consumer) {
		BlockPos to = new BlockPos(bound.boundX.orElse(sizeX), bound.boundY.orElse(sizeY), bound.boundZ.orElse(sizeZ));

		for (int xi = 0; xi < Math.min(to.subtract(from).getX(), this.sizeX); xi++) {

			for (int yi = 0; yi < Math.min(to.subtract(from).getY(), this.sizeY); yi++) {

				for (int zi = 0; zi < Math.min(to.subtract(from).getZ(), this.sizeZ); zi++) {
					BlockPos pos = new BlockPos(xi, yi, zi);
					Pair<BlockState, Optional<NbtCompound>> pair = this.positions.get(pos.add(offset));

					if (pair == null) {
						pair = Pair.of(Blocks.STRUCTURE_VOID.getDefaultState(), Optional.empty());
					}

					BlockState state = pair.getFirst();
					Optional<NbtCompound> nbt = pair.getSecond();

					if (state != null) {
						consumer.accept(from.add(pos), state, nbt);
					}

				}

			}

		}

		return this;
	}

	public NbtPlacerUtil spawnEntities(ChunkRegion region, BlockPos pos, Manipulation manipulation,
			Function<NbtCompound, NbtCompound> modifier) {
		return spawnEntities(region, BlockPos.ORIGIN, pos, new Bound(pos.add(this.sizeVector)), manipulation, modifier);
	}

	public NbtPlacerUtil spawnEntities(ChunkRegion region, BlockPos offset, BlockPos from, Bound bound,
			Manipulation manipulation, Function<NbtCompound, NbtCompound> modifier) {
		this.entities.forEach((nbtElement) -> spawnEntity(nbtElement, region, offset, from, bound, manipulation, modifier));
		return this;
	}

	public NbtPlacerUtil spawnEntity(NbtElement nbtElement, ChunkRegion region, BlockPos offset, BlockPos from, Bound bound,
			Manipulation manipulation, Function<NbtCompound, NbtCompound> modifier) {
		BlockPos to = new BlockPos(bound.boundX.orElse(sizeX), bound.boundY.orElse(sizeY), bound.boundZ.orElse(sizeZ));

		NbtCompound entityCompound = (NbtCompound) nbtElement;
		NbtList nbtPos = entityCompound.getList("pos", 6);
		Vec3d relativeLocation = mirror(
			rotate(new Vec3d(nbtPos.getDouble(0), nbtPos.getDouble(1), nbtPos.getDouble(2)), manipulation.getRotation()),
			manipulation.getMirror()).subtract(Vec3d.of(lowestPos));
		Vec3d realPosition = relativeLocation.add(Vec3d.of(from.subtract(offset)));
		BlockPos min = offset;
		BlockPos max = to.subtract(from).add(offset);

		if (!((relativeLocation.getX() < max.getX() && relativeLocation.getX() >= min.getX()) && (relativeLocation
			.getY() < max.getY() && relativeLocation
				.getY() >= min.getY()) && (relativeLocation.getZ() < max.getZ() && relativeLocation.getZ() >= min.getZ()))) {
			return this;
		}

		NbtCompound nbt = entityCompound.getCompound("nbt").copy();
		nbt.remove("Pos");
		nbt.remove("UUID");
		NbtList posList = new NbtList();
		posList.add(NbtDouble.of(realPosition.x));
		posList.add(NbtDouble.of(realPosition.y));
		posList.add(NbtDouble.of(realPosition.z));
		nbt.put("Pos", posList);
		NbtList rotationList = new NbtList();
		NbtList entityRotationList = nbt.getList("Rotation", 5);
		float yawRotation = applyMirror(applyRotation(entityRotationList.getFloat(0), manipulation.getRotation()),
			manipulation.getMirror());
		rotationList.add(NbtFloat.of(yawRotation));
		rotationList.add(NbtFloat.of(entityRotationList.getFloat(1)));
		nbt.remove("Rotation");
		nbt.put("Rotation", rotationList);

		if (nbt.contains("facing")) {
			Direction dir = mirror(manipulation.getRotation().rotate(Direction.fromHorizontal(nbt.getByte("facing"))),
				manipulation.getMirror());
			nbt.remove("facing");
			nbt.putByte("facing", (byte) dir.getHorizontal());
		}

		if (nbt.contains("Facing")) {
			Direction dir = mirror(manipulation.getRotation().rotate(Direction.byId(nbt.getByte("Facing"))),
				manipulation.getMirror());
			nbt.remove("Facing");
			nbt.putByte("Facing", (byte) dir.getId());
		}

		if (nbt.contains("TileX", 3) && nbt.contains("TileY", 3) && nbt.contains("TileZ", 3)) {
			nbt.remove("TileX");
			nbt.remove("TileY");
			nbt.remove("TileZ");
			nbt.putInt("TileX", MathHelper.floor(realPosition.x));
			nbt.putInt("TileY", MathHelper.floor(realPosition.y));
			nbt.putInt("TileZ", MathHelper.floor(realPosition.z));
		}

		Optional<Entity> optionalEntity = getEntity(region, modifier.apply(nbt));

		if (optionalEntity.isPresent()) {
			Entity entity = optionalEntity.get();
			entity.refreshPositionAndAngles(realPosition.x, realPosition.y, realPosition.z, yawRotation, entity.getPitch());

			if (entity instanceof AbstractDecorationEntity deco) {

				if (deco instanceof PaintingEntity) {
					double newX = realPosition.getX() - (deco.getWidthPixels() % 32 == 0 ? 0.5 : 0.0) * deco
						.getHorizontalFacing()
						.rotateYCounterclockwise()
						.getOffsetX();
					double newY = realPosition.getY() - (deco.getHeightPixels() % 32 == 0 ? 0.5 : 0.0);
					double newZ = realPosition.getZ() - (deco.getWidthPixels() % 32 == 0 ? 0.5 : 0.0) * deco
						.getHorizontalFacing()
						.rotateYCounterclockwise()
						.getOffsetZ();
					newX += deco.getHorizontalFacing().getOffsetX() * 0.46875D;
					newZ += deco.getHorizontalFacing().getOffsetZ() * 0.46875D;
					newX -= 0.5;
					newY -= 0.5;
					newZ -= 0.5;
					deco.setPosition(newX, newY, newZ);
				}

			}

			region.spawnEntity(entity);
		}

		return this;
	}

	@SuppressWarnings("deprecation")
	public static Optional<Entity> getEntity(ChunkRegion region, NbtCompound nbt) {

		try {
			return EntityType.getEntityFromNbt(nbt, region.toServerWorld());
		} catch (Exception e) {
			LOGGER.error("Failed to parse entity {}", nbt);
			return Optional.empty();
		}

	}

	public static Vec3d rotate(Vec3d in, BlockRotation rotation) {

		switch (rotation) {
			case NONE:
			default:
				return in;
			case CLOCKWISE_90:
				return new Vec3d(-in.getZ(), in.getY(), in.getX());
			case CLOCKWISE_180:
				return new Vec3d(-in.getX(), in.getY(), -in.getZ());
			case COUNTERCLOCKWISE_90:
				return new Vec3d(in.getZ(), in.getY(), -in.getX());
		}

	}

	public static Vec3d mirror(Vec3d in, BlockMirror mirror) {

		switch (mirror) {
			case NONE:
			default:
				return in;
			case LEFT_RIGHT:
				return new Vec3d(in.getX(), in.getY(), -in.getZ());
			case FRONT_BACK:
				return new Vec3d(-in.getX(), in.getY(), in.getZ());
		}

	}

	public static BlockPos rotate(BlockPos in, BlockRotation rotation) {

		switch (rotation) {
			case NONE:
			default:
				return in;
			case CLOCKWISE_90:
				return new BlockPos(-in.getZ(), in.getY(), in.getX());
			case CLOCKWISE_180:
				return new BlockPos(-in.getX(), in.getY(), -in.getZ());
			case COUNTERCLOCKWISE_90:
				return new BlockPos(in.getZ(), in.getY(), -in.getX());
		}

	}

	public static BlockPos mirror(BlockPos in, BlockMirror mirror) {

		switch (mirror) {
			case NONE:
			default:
				return in;
			case LEFT_RIGHT:
				return new BlockPos(in.getX(), in.getY(), -in.getZ());
			case FRONT_BACK:
				return new BlockPos(-in.getX(), in.getY(), in.getZ());
		}

	}

	public static BlockPos transformSize(BlockPos in, BlockRotation rotation, BlockMirror mirror) {
		BlockPos origin = BlockPos.ORIGIN;
		BlockPos xPin = mirror(rotate(new BlockPos(in.getX(), 0, 0), rotation), mirror);
		BlockPos zPin = mirror(rotate(new BlockPos(0, 0, in.getZ()), rotation), mirror);
		BlockPos pin = mirror(rotate(new BlockPos(in.getX(), 0, in.getZ()), rotation), mirror);
		return findBottomLeftVertex(origin, xPin, zPin, pin);
	}

	public static BlockPos findBottomLeftVertex(BlockPos v1, BlockPos v2, BlockPos v3, BlockPos v4) {
		BlockPos[] vertices = { v1, v2, v3, v4 };
		Arrays.sort(vertices, Comparator.comparingInt(BlockPos::getX));
		Arrays.sort(vertices, Comparator.comparingInt(BlockPos::getZ));
		return vertices[0];
	}

	public Direction mirror(Direction in, BlockMirror mirror) {

		switch (mirror) {
			case LEFT_RIGHT:
				if (in.getAxis().equals(Direction.Axis.Z)) {
					return in.getOpposite();
				}
				break;
			case FRONT_BACK:
				if (in.getAxis().equals(Direction.Axis.X)) {
					return in.getOpposite();
				}
				break;
			case NONE:
			default:
				break;
		}

		return in;
	}

	public float applyRotation(float in, BlockRotation rotation) {
		float f = MathHelper.wrapDegrees(in);

		switch (rotation) {
			case CLOCKWISE_180:
				return f + 180.0F;
			case COUNTERCLOCKWISE_90:
				return f + 270.0F;
			case CLOCKWISE_90:
				return f + 90.0F;
			default:
				return f;
		}

	}

	public float applyMirror(float in, BlockMirror mirror) {
		float f = MathHelper.wrapDegrees(in);

		switch (mirror) {
			case LEFT_RIGHT:
				return 180.0F - f;
			case FRONT_BACK:
				return -f;
			default:
				return f;
		}

	}

	public static Vec3d abs(Vec3d in) {
		return new Vec3d(Math.abs(in.getX()), Math.abs(in.getY()), Math.abs(in.getZ()));
	}

	public static NbtList createNbtIntList(int... ints) {
		NbtList nbtList = new NbtList();
		int size = ints.length;

		for (int j = 0; j < size; ++j) {
			int i = ints[j];
			nbtList.add(NbtInt.of(i));
		}

		return nbtList;
	}

	public static class Bound {

		final Optional<Integer> boundX;
		final Optional<Integer> boundY;
		final Optional<Integer> boundZ;

		public Bound(Optional<Integer> boundX, Optional<Integer> boundY, Optional<Integer> boundZ) {
			this.boundX = boundX;
			this.boundY = boundY;
			this.boundZ = boundZ;
		}

		public Bound(Integer boundX, Integer boundY, Integer boundZ) {
			this(Optional.ofNullable(boundX), Optional.ofNullable(boundY), Optional.ofNullable(boundZ));
		}

		public Bound(Vec3i pos) {
			this(pos.getX(), pos.getY(), pos.getZ());
		}

		public Optional<Integer> getBoundX() {
			return boundX;
		}

		public Optional<Integer> getBoundY() {
			return boundY;
		}

		public Optional<Integer> getBoundZ() {
			return boundZ;
		}

		public boolean isBoundX() {
			return boundX.isPresent();
		}

		public boolean isBoundY() {
			return boundY.isPresent();
		}

		public boolean isBoundZ() {
			return boundZ.isPresent();
		}

	}

}
