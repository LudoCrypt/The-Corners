package net.ludocrypt.corners.util;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import com.mojang.datafixers.util.Pair;

import net.ludocrypt.corners.TheCorners;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;

public class NbtPlacerUtil {

	public final HashMap<Integer, BlockState> palette;
	public final HashMap<BlockPos, Integer> positions;
	public final int sizeX;
	public final int sizeY;
	public final int sizeZ;

	public NbtPlacerUtil(HashMap<Integer, BlockState> palette, HashMap<BlockPos, Integer> positions, int sizeX, int sizeY, int sizeZ) {
		this.palette = palette;
		this.positions = positions;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
	}

	public NbtPlacerUtil(HashMap<Integer, BlockState> palette, HashMap<BlockPos, Integer> positions, BlockPos sizePos) {
		this(palette, positions, sizePos.getX(), sizePos.getY(), sizePos.getZ());
	}

	public static Optional<NbtPlacerUtil> load(ResourceManager manager, Identifier id) {
		return load(manager, id, BlockRotation.NONE);
	}

	public static Optional<NbtPlacerUtil> load(ResourceManager manager, Identifier id, BlockRotation rotation) {
		try {
			Optional<NbtCompound> nbtOptional = loadNbtFromFile(manager, id);
			if (nbtOptional.isPresent()) {
				NbtCompound nbt = nbtOptional.get();

				NbtList paletteList = nbt.getList("palette", 10);
				HashMap<Integer, BlockState> palette = new HashMap<Integer, BlockState>(paletteList.size());
				List<NbtCompound> paletteCompoundList = paletteList.stream().filter(nbtElement -> nbtElement instanceof NbtCompound).map(element -> (NbtCompound) element).toList();
				for (int i = 0; i < paletteCompoundList.size(); i++) {
					palette.put(i, NbtHelper.toBlockState(paletteCompoundList.get(i)).rotate(rotation));
				}

				NbtList sizeList = nbt.getList("size", 3);
				BlockPos sizeVectorRotated = new BlockPos(sizeList.getInt(0), sizeList.getInt(1), sizeList.getInt(2)).rotate(rotation);
				BlockPos sizeVector = new BlockPos(Math.abs(sizeVectorRotated.getX()), Math.abs(sizeVectorRotated.getY()), Math.abs(sizeVectorRotated.getZ()));

				NbtList positionsList = nbt.getList("blocks", 10);
				HashMap<BlockPos, Integer> positions = new HashMap<BlockPos, Integer>(positionsList.size());
				List<Pair<BlockPos, Integer>> positionsPairList = positionsList.stream().filter(nbtElement -> nbtElement instanceof NbtCompound).map(element -> (NbtCompound) element).map((nbtCompound) -> Pair.of(new BlockPos(nbtCompound.getList("pos", 3).getInt(0), nbtCompound.getList("pos", 3).getInt(1), nbtCompound.getList("pos", 3).getInt(2)).rotate(rotation), nbtCompound.getInt("state"))).sorted(Comparator.comparing((pair) -> pair.getFirst().getX())).sorted(Comparator.comparing((pair) -> pair.getFirst().getY())).sorted(Comparator.comparing((pair) -> pair.getFirst().getZ())).toList();
				positionsPairList.forEach((pair) -> positions.put(pair.getFirst().subtract(positionsPairList.get(0).getFirst()), pair.getSecond()));

				return Optional.of(new NbtPlacerUtil(palette, positions, sizeVector));
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

	public static void generateNbt(ChunkRegion region, Chunk chunk, Identifier id, BlockPos offset, BiConsumer<BlockPos, BlockState> consumer, BlockRotation rotation) {
		ServerWorld world = region.toServerWorld();
		BlockPos pos = chunk.getPos().getStartPos();
		ResourceManager resourceManager = world.getServer().getResourceManager();
		Optional<NbtPlacerUtil> optional = NbtPlacerUtil.load(resourceManager, id, rotation);
		if (optional.isPresent()) {
			NbtPlacerUtil nbt = optional.get();
			if ((pos.getX() - offset.getX() <= nbt.sizeX) && (pos.getZ() - offset.getZ() <= nbt.sizeZ) && (pos.getY() - offset.getY() <= nbt.sizeY)) {
				for (int x = pos.getX(); x < pos.getX() + 16; x++) {
					if (x >= offset.getX() && x < nbt.sizeX + offset.getX()) {
						for (int z = pos.getZ(); z < pos.getZ() + 16; z++) {
							if (z >= offset.getZ() && z < nbt.sizeZ + offset.getZ()) {
								for (int y = offset.getY(); y < offset.getY() + nbt.sizeY; y++) {
									BlockPos realPos = new BlockPos(x, y, z);
									BlockPos inPos = new BlockPos(x - offset.getX(), y - offset.getY(), z - offset.getZ());
									try {
										int palette = nbt.positions.get(inPos);
										BlockState state = nbt.palette.get(palette);
										consumer.accept(realPos, state);
									} catch (Exception e) {
										TheCorners.LOGGER.warn("InPos {} (realPos {}) has mismatched or incorrect block! Extra info: size {}, positions {}", inPos, realPos, new BlockPos(nbt.sizeX, nbt.sizeY, nbt.sizeZ), nbt.positions);
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static void generateNbt(ChunkRegion region, Chunk chunk, Identifier id) {
		generateNbt(region, chunk, id, BlockPos.ORIGIN);
	}

	public static void generateNbt(ChunkRegion region, Chunk chunk, Identifier id, BlockPos offsetPos) {
		generateNbt(region, chunk, id, offsetPos, (pos, state) -> chunk.setBlockState(pos, state, false), BlockRotation.NONE);
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
