package net.ludocrypt.corners.util;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.util.TriConsumer;

import com.mojang.datafixers.util.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkRegion;

public class NbtPlacerUtil {

	public final NbtCompound storedNbt;
	public final HashMap<BlockPos, Pair<BlockState, NbtCompound>> positions;
	public final NbtList entities;
	public final BlockPos lowestPos;
	public final int sizeX;
	public final int sizeY;
	public final int sizeZ;

	public NbtPlacerUtil(NbtCompound storedNbt, HashMap<BlockPos, Pair<BlockState, NbtCompound>> positions, NbtList entities, BlockPos lowestPos, int sizeX, int sizeY, int sizeZ) {
		this.storedNbt = storedNbt;
		this.positions = positions;
		this.entities = entities;
		this.lowestPos = lowestPos;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
	}

	public NbtPlacerUtil(NbtCompound storedNbt, HashMap<BlockPos, Pair<BlockState, NbtCompound>> positions, NbtList entities, BlockPos lowestPos, BlockPos sizePos) {
		this(storedNbt, positions, entities, lowestPos, sizePos.getX(), sizePos.getY(), sizePos.getZ());
	}

	public NbtPlacerUtil rotate(BlockRotation rotation) {
		NbtList paletteList = storedNbt.getList("palette", 10);
		HashMap<Integer, BlockState> palette = new HashMap<Integer, BlockState>(paletteList.size());
		List<NbtCompound> paletteCompoundList = paletteList.stream().filter(nbtElement -> nbtElement instanceof NbtCompound).map(element -> (NbtCompound) element).toList();
		for (int i = 0; i < paletteCompoundList.size(); i++) {
			palette.put(i, NbtHelper.toBlockState(paletteCompoundList.get(i)).rotate(rotation));
		}

		NbtList sizeList = storedNbt.getList("size", 3);
		BlockPos sizeVectorRotated = new BlockPos(sizeList.getInt(0), sizeList.getInt(1), sizeList.getInt(2)).rotate(rotation);
		BlockPos sizeVector = new BlockPos(Math.abs(sizeVectorRotated.getX()), Math.abs(sizeVectorRotated.getY()), Math.abs(sizeVectorRotated.getZ()));

		NbtList positionsList = storedNbt.getList("blocks", 10);
		HashMap<BlockPos, Pair<BlockState, NbtCompound>> positions = new HashMap<BlockPos, Pair<BlockState, NbtCompound>>(positionsList.size());
		List<Pair<BlockPos, Pair<BlockState, NbtCompound>>> positionsPairList = positionsList.stream().filter(nbtElement -> nbtElement instanceof NbtCompound).map(element -> (NbtCompound) element).map((nbtCompound) -> Pair.of(new BlockPos(nbtCompound.getList("pos", 3).getInt(0), nbtCompound.getList("pos", 3).getInt(1), nbtCompound.getList("pos", 3).getInt(2)).rotate(rotation), Pair.of(palette.get(nbtCompound.getInt("state")), nbtCompound.getCompound("nbt")))).sorted(Comparator.comparing((pair) -> pair.getFirst().getX())).sorted(Comparator.comparing((pair) -> pair.getFirst().getY())).sorted(Comparator.comparing((pair) -> pair.getFirst().getZ())).toList();
		positionsPairList.forEach((pair) -> positions.put(pair.getFirst().subtract(positionsPairList.get(0).getFirst()), pair.getSecond()));

		return new NbtPlacerUtil(storedNbt, positions, storedNbt.getList("entities", 10), positionsPairList.get(0).getFirst(), sizeVector);
	}

	public static Optional<NbtPlacerUtil> load(ResourceManager manager, Identifier id) {
		try {
			Optional<NbtCompound> nbtOptional = loadNbtFromFile(manager, id);
			if (nbtOptional.isPresent()) {
				NbtCompound nbt = nbtOptional.get();

				NbtList paletteList = nbt.getList("palette", 10);
				HashMap<Integer, BlockState> palette = new HashMap<Integer, BlockState>(paletteList.size());
				List<NbtCompound> paletteCompoundList = paletteList.stream().filter(nbtElement -> nbtElement instanceof NbtCompound).map(element -> (NbtCompound) element).toList();
				for (int i = 0; i < paletteCompoundList.size(); i++) {
					palette.put(i, NbtHelper.toBlockState(paletteCompoundList.get(i)));
				}

				NbtList sizeList = nbt.getList("size", 3);
				BlockPos sizeVectorRotated = new BlockPos(sizeList.getInt(0), sizeList.getInt(1), sizeList.getInt(2));
				BlockPos sizeVector = new BlockPos(Math.abs(sizeVectorRotated.getX()), Math.abs(sizeVectorRotated.getY()), Math.abs(sizeVectorRotated.getZ()));

				NbtList positionsList = nbt.getList("blocks", 10);
				HashMap<BlockPos, Pair<BlockState, NbtCompound>> positions = new HashMap<BlockPos, Pair<BlockState, NbtCompound>>(positionsList.size());
				List<Pair<BlockPos, Pair<BlockState, NbtCompound>>> positionsPairList = positionsList.stream().filter(nbtElement -> nbtElement instanceof NbtCompound).map(element -> (NbtCompound) element).map((nbtCompound) -> Pair.of(new BlockPos(nbtCompound.getList("pos", 3).getInt(0), nbtCompound.getList("pos", 3).getInt(1), nbtCompound.getList("pos", 3).getInt(2)), Pair.of(palette.get(nbtCompound.getInt("state")), nbtCompound.getCompound("nbt")))).sorted(Comparator.comparing((pair) -> pair.getFirst().getX())).sorted(Comparator.comparing((pair) -> pair.getFirst().getY())).sorted(Comparator.comparing((pair) -> pair.getFirst().getZ())).toList();
				positionsPairList.forEach((pair) -> positions.put(pair.getFirst().subtract(positionsPairList.get(0).getFirst()), pair.getSecond()));

				return Optional.of(new NbtPlacerUtil(nbt, positions, nbt.getList("entities", 10), positionsPairList.get(0).getFirst(), sizeVector));
			}

			throw new NullPointerException();
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	public static Optional<NbtCompound> loadNbtFromFile(ResourceManager manager, Identifier id) {
		try {
			return Optional.ofNullable(readStructure(manager.getResource(id)));
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	public static NbtCompound readStructure(Resource resource) throws IOException {
		NbtCompound nbt = NbtIo.readCompressed(resource.getInputStream());
		resource.close();
		return nbt;
	}

	public NbtPlacerUtil generateNbt(ChunkRegion region, BlockPos at, TriConsumer<BlockPos, BlockState, NbtCompound> consumer) {
		for (int xi = 0; xi < this.sizeX; xi++) {
			for (int yi = 0; yi < this.sizeY; yi++) {
				for (int zi = 0; zi < this.sizeZ; zi++) {
					Pair<BlockState, NbtCompound> pair = this.positions.get(new BlockPos(xi, yi, zi));
					BlockState state = pair.getFirst();
					NbtCompound nbt = pair.getSecond();
					consumer.accept(at.add(xi, yi, zi), state == null ? Blocks.BARRIER.getDefaultState() : state, nbt);
				}
			}
		}
		return this;
	}

	public NbtPlacerUtil spawnEntities(ChunkRegion region, BlockPos pos, BlockRotation rotation) {
		this.entities.forEach((nbtElement) -> {
			NbtCompound entityCompound = (NbtCompound) nbtElement;
			NbtList nbtPos = entityCompound.getList("blockPos", 3);
			Vec3d realPosition = rotate(new Vec3d(nbtPos.getInt(0), nbtPos.getInt(1), nbtPos.getInt(2)), rotation).subtract(Vec3d.of(lowestPos)).add(pos.getX(), pos.getY(), pos.getZ());

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
			float yawRotation = applyRotation(entityRotationList.getFloat(0), rotation);
			rotationList.add(NbtFloat.of(yawRotation));
			rotationList.add(NbtFloat.of(entityRotationList.getFloat(1)));
			nbt.remove("Rotation");
			nbt.put("Rotation", rotationList);

			if (nbt.contains("Facing")) {
				Direction dir = rotation.rotate(Direction.fromHorizontal(nbt.getByte("Facing")));
				nbt.remove("Facing");
				nbt.putByte("Facing", (byte) dir.getHorizontal());
			}

			getEntity(region, nbt).ifPresent((entity) -> {
				entity.refreshPositionAndAngles(realPosition.x, realPosition.y, realPosition.z, yawRotation, entity.getPitch());
				region.spawnEntity(entity);
			});
		});
		return this;
	}

	public static Optional<Entity> getEntity(ChunkRegion region, NbtCompound nbt) {
		try {
			return EntityType.getEntityFromNbt(nbt, region.toServerWorld());
		} catch (Exception e) {
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

	public static Vec3d abs(Vec3d in) {
		return new Vec3d(Math.abs(in.getX()), Math.abs(in.getY()), Math.abs(in.getZ()));
	}

	public static NbtList createNbtIntList(int... ints) {
		NbtList nbtList = new NbtList();
		int[] var3 = ints;
		int var4 = ints.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			int i = var3[var5];
			nbtList.add(NbtInt.of(i));
		}

		return nbtList;
	}

}
