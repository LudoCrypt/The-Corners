package net.ludocrypt.corners.world.chunk;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.block.StructureHelperBlock;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerWorlds;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Face;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.ludocrypt.limlib.api.world.maze.MazeGenerator;
import net.ludocrypt.limlib.api.world.maze.storage.MazeStorageProvider;
import net.ludocrypt.limlib.api.world.maze.types.MazeTypes;
import net.ludocrypt.limlib.api.world.nbt.NbtGroup;
import net.ludocrypt.limlib.api.world.nbt.NbtPlacerUtil.Bound;
import net.ludocrypt.limlib.api.world.nbt.NbtTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CommunalCorridorsChunkGenerator extends AbstractNbtChunkGenerator implements MazeStorageProvider {

	public static final Codec<CommunalCorridorsChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.populationSource;
		}), NbtGroup.CODEC.fieldOf("group").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.nbtGroup;
		}), Codec.INT.fieldOf("maze_width").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeWidth;
		}), Codec.INT.fieldOf("maze_height").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeHeight;
		}), Codec.INT.fieldOf("maze_thickness").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeThickness;
		}), Codec.LONG.fieldOf("seed_modifier").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeSeedModifier;
		})).apply(instance, instance.stable(CommunalCorridorsChunkGenerator::new));
	});

	private int mazeWidth;
	private int mazeHeight;
	private int mazeThickness;
	private long mazeSeedModifier;

	private MazeGenerator mazeGenerator;

	public CommunalCorridorsChunkGenerator(BiomeSource biomeSource, NbtGroup group, int mazeWidth, int mazeHeight,
			int mazeThickness, long mazeSeedModifier) {
		super(biomeSource, group);
		this.mazeWidth = mazeWidth;
		this.mazeHeight = mazeHeight;
		this.mazeThickness = mazeThickness;
		this.mazeSeedModifier = mazeSeedModifier;
		this.mazeGenerator = new MazeGenerator(mazeWidth, mazeHeight, mazeThickness, mazeThickness, mazeSeedModifier);
	}

	public static NbtGroup createGroup() {
		NbtGroup.Builder builder = NbtGroup.Builder
			.create(TheCorners.id(CornerWorlds.COMMUNAL_CORRIDORS))
			.with("communal_corridors_maze", 1, 255)
			.with("communal_corridors_frame", 1, 3)
			.with("communal_corridors_maze_big", 1, 18);
		return builder.build();
	}

	@Override
	public void loadTags() {

		for (int i = 0; i < 16; i++) {
			CellState state = new CellState();

			state.up((i & 0b0001) != 0);
			state.down((i & 0b0010) != 0);
			state.left((i & 0b0100) != 0);
			state.right((i & 0b1000) != 0);

			this.tags.matching(state.toString(), (tag, nbt) -> {

				if (tag.equals("connections")) {

					for (Face face : Face.values()) {

						if (nbt.contains(face.asString())) {

							if (nbt.getList(face.asString(), NbtElement.BYTE_TYPE).size() != 1) {
								return false;
							}

							if (((NbtByte) nbt.getList(face.asString(), NbtElement.BYTE_TYPE).get(0))
								.byteValue() != (state.goes(face) ? 1 : 0)) {
								return false;
							}

						}

					}

					return true;
				}

				return false;
			});
		}

		this.tags.matching("frame_open", (tag, nbt) -> {

			if (tag.equals("frame")) {
				return nbt.getByte("open") == 1;
			}

			return false;
		});

		this.tags.matching("frame_closed", (tag, nbt) -> {

			if (tag.equals("frame")) {
				return nbt.getByte("open") == 0;
			}

			return false;
		});
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor,
			ServerWorld world, ChunkGenerator generator, StructureTemplateManager structureTemplateManager,
			ServerLightingProvider lightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> fullChunkConverter, List<Chunk> chunks,
			Chunk chunk) {
		this.mazeGenerator
			.generateMaze(new Vec2i(chunk.getPos().getStartX(), chunk.getPos().getStartZ()), region,
				(r, mazePos, size, random) -> {
					return MazeTypes.depthFirst(mazeWidth, mazeHeight, random);
//					return createLevel2Maze(this.mazeWidth, this.mazeHeight, random, this.nbtGroup, this.tags);
				}, (r, pos, mazePos, maze, state, thickness, random) -> {
//					RandomGenerator cellRandom = RandomGenerator
//						.createLegacy(region.getSeed() + LimlibHelper.blockSeed(pos.toBlock()));
//
//					Set<Identifier> options = this.tags.matching(state.toString());
//
//					if (!options.isEmpty()) {
//						this
//							.generateNbt(region, pos.toBlock(),
//								options.stream().toList().get(cellRandom.nextInt(options.size())),
//								Manipulation.CLOCKWISE_90);
//					}

					if (state.getExtra().containsKey("offset")) {
						NbtCompound offset = state.getExtra().get("offset");

						List<Pair<BlockPos, BlockRotation>> doors = Lists.newArrayList();

						boolean flip = state.getExtra().get("flip").getBoolean("flip");

						this
							.generateNbt(region,
								new BlockPos(offset.getInt("x") * thickness.getX(), 0,
									offset.getInt("y") * thickness.getY()),
								pos.toBlock(),
								new Bound(pos.getX() + (2 * thickness.getX()), null, pos.getY() + (2 * thickness.getY())),
								new Identifier(state.getExtra().get("room").getString("id")),
								Manipulation.CLOCKWISE_90
									.manipulate(flip ? Manipulation.TOP_LEFT_BOTTOM_RIGHT : Manipulation.NONE),
								(sPos, sState, optionalNbt) -> {

									if (sState.isOf(CornerBlocks.STRUCTURE_HELPER)) {
										doors.add(Pair.of(sPos, sState.get(StructureHelperBlock.ROTATION)));
										return;
									}

									this.modifyStructure(region, sPos, sState, optionalNbt);
								});

						for (Pair<BlockPos, BlockRotation> door : doors) {
							Vec2i cell = this.mazeGenerator
								.getInlineCell(new Vec2i(door.getFirst().getX(), door.getFirst().getZ()));

							Face face = switch (door.getSecond()) {
								case CLOCKWISE_180 -> Face.DOWN;
								case CLOCKWISE_90 -> Face.RIGHT;
								case COUNTERCLOCKWISE_90 -> Face.LEFT;
								case NONE -> Face.UP;
							};

							List<Identifier> frames = this.tags
								.matching("frame_" + (maze.cellState(cell).goes(face) ? "open" : "closed"))
								.stream()
								.toList();

							Identifier frame = frames.get(random.nextInt(frames.size()));
							NbtList frameOffset = this.tags.tags
								.get("communal_corridors_frame")
								.get(frame)
								.get("offset")
								.getList("offset", 3);

							BlockPos frameOffsetPos = new BlockPos(frameOffset.getInt(0), frameOffset.getInt(1),
								frameOffset.getInt(2));

							switch (door.getSecond()) {
								case CLOCKWISE_90:
								case COUNTERCLOCKWISE_90:
									frameOffsetPos = new BlockPos(frameOffsetPos.getZ(), frameOffsetPos.getY(),
										frameOffsetPos.getX());
									break;
								default:
									break;
							}

							this
								.generateNbt(region, door.getFirst().subtract(frameOffsetPos), frame,
									Manipulation.of(door.getSecond()));

						}

					}

					if (state.goes() || state.getPosition().getX() == 0 || state.getPosition().getY() == 0) {
						region
							.setBlockState(pos.add(3, 3).toBlock().up(64), Blocks.RED_CONCRETE.getDefaultState(),
								Block.FORCE_STATE);
					}

					if (state.goesRight() || state.getPosition().getX() == 0) {
						region
							.setBlockState(pos.add(3, 4).toBlock().up(64), Blocks.RED_CONCRETE.getDefaultState(),
								Block.FORCE_STATE);
						region
							.setBlockState(pos.add(3, 5).toBlock().up(64), Blocks.RED_CONCRETE.getDefaultState(),
								Block.FORCE_STATE);
						region
							.setBlockState(pos.add(3, 6).toBlock().up(64), Blocks.RED_CONCRETE.getDefaultState(),
								Block.FORCE_STATE);
						region
							.setBlockState(pos.add(3, 7).toBlock().up(64), Blocks.RED_CONCRETE.getDefaultState(),
								Block.FORCE_STATE);
					}

					if (state.goesLeft() || state.getPosition().getX() == 0) {
						region
							.setBlockState(pos.add(3, 2).toBlock().up(64), Blocks.RED_CONCRETE.getDefaultState(),
								Block.FORCE_STATE);
						region
							.setBlockState(pos.add(3, 1).toBlock().up(64), Blocks.RED_CONCRETE.getDefaultState(),
								Block.FORCE_STATE);
						region
							.setBlockState(pos.add(3, 0).toBlock().up(64), Blocks.RED_CONCRETE.getDefaultState(),
								Block.FORCE_STATE);
					}

					if (state.goesUp() || state.getPosition().getY() == 0) {
						region
							.setBlockState(pos.add(4, 3).toBlock().up(64), Blocks.RED_CONCRETE.getDefaultState(),
								Block.FORCE_STATE);
						region
							.setBlockState(pos.add(5, 3).toBlock().up(64), Blocks.RED_CONCRETE.getDefaultState(),
								Block.FORCE_STATE);
						region
							.setBlockState(pos.add(6, 3).toBlock().up(64), Blocks.RED_CONCRETE.getDefaultState(),
								Block.FORCE_STATE);
						region
							.setBlockState(pos.add(7, 3).toBlock().up(64), Blocks.RED_CONCRETE.getDefaultState(),
								Block.FORCE_STATE);
					}

					if (state.goesDown() || state.getPosition().getY() == 0) {
						region
							.setBlockState(pos.add(2, 3).toBlock().up(64), Blocks.RED_CONCRETE.getDefaultState(),
								Block.FORCE_STATE);
						region
							.setBlockState(pos.add(1, 3).toBlock().up(64), Blocks.RED_CONCRETE.getDefaultState(),
								Block.FORCE_STATE);
						region
							.setBlockState(pos.add(0, 3).toBlock().up(64), Blocks.RED_CONCRETE.getDefaultState(),
								Block.FORCE_STATE);
					}

				});

		return CompletableFuture.completedFuture(chunk);
	}

	public static MazeComponent createLevel2Maze(int width, int height, RandomGenerator random, NbtGroup group,
			NbtTags tags) {
		MazeComponent maze = new MazeComponent(width, height);

		int roomCount = 4;
		int maxIter = 10;

		List<String> rooms = Lists.newArrayList(group.getGroups().get("communal_corridors_maze_big"));
		Collections.shuffle(rooms, new Random(random.nextLong()));

		tryRoom: for (int i = 0; i < roomCount && maxIter > 0; i++) {
			Identifier room = group.nbtId("communal_corridors_maze_big", rooms.get(Math.min(i, rooms.size() - 1)));
			NbtCompound connections = tags.tags
				.get("communal_corridors_maze_big")
				.get(room)
				.getOrDefault("connections", new NbtCompound());

			boolean flip = random.nextBoolean();

			NbtList upConnections = connections.getList(flip ? "right" : "up", NbtElement.BYTE_TYPE);
			NbtList downConnections = connections.getList(flip ? "left" : "down", NbtElement.BYTE_TYPE);
			NbtList leftConnections = connections.getList(flip ? "down" : "left", NbtElement.BYTE_TYPE);
			NbtList rightConnections = connections.getList(flip ? "up" : "right", NbtElement.BYTE_TYPE);

			int roomWidth = upConnections.size();
			int roomHeight = leftConnections.size();

			int leeway = 4;
			int leewayWidth = width - roomWidth - leeway - leeway;
			int leewayHeight = height - roomHeight - leeway - leeway;

			if (leewayWidth > 0 && leewayHeight > 0) {

				int xOffset = random.nextInt(leewayWidth) + leeway;
				int yOffset = random.nextInt(leewayHeight) + leeway;

				for (int x = xOffset - leeway; x < xOffset + roomWidth + leeway; x++) {

					for (int y = yOffset - leeway; y < yOffset + roomHeight + leeway; y++) {

						if (!maze.cellState(x, y).getExtra().isEmpty()) {
							i--;
							maxIter--;
							continue tryRoom;
						}

					}

				}

				for (int x = 0; x < roomWidth; x++) {

					for (int y = 0; y < roomHeight; y++) {
						CellState state = maze.cellState(x + xOffset, y + yOffset);

						if (x % 2 == 0 && y % 2 == 0) {
							NbtCompound offset = new NbtCompound();
							offset.putInt("x", x);
							offset.putInt("y", y);
							state.append("offset", offset);

							NbtCompound roomId = new NbtCompound();
							roomId.putString("id", room.toString());
							state.append("room", roomId);

							NbtCompound flipTag = new NbtCompound();
							flipTag.putBoolean("flip", flip);
							state.append("flip", flipTag);
						}

						if (x == 0) {

							byte goes = ((NbtByte) downConnections.get(y)).byteValue();

							if (goes == 1 || (goes == 2 && random.nextBoolean())) {
								state.down();
							}

							state.up();
						} else if (x + 1 == roomWidth) {
							byte goes = ((NbtByte) upConnections.get(y)).byteValue();

							if (goes == 1 || (goes == 2 && random.nextBoolean())) {
								state.up();
							}

							state.down();
						}

						if (y == 0) {
							byte goes = ((NbtByte) leftConnections.get(x)).byteValue();

							if (goes == 1 || (goes == 2 && random.nextBoolean())) {
								state.left();
							}

							state.right();
						} else if (y + 1 == roomHeight) {
							byte goes = ((NbtByte) rightConnections.get(x)).byteValue();

							if (goes == 1 || (goes == 2 && random.nextBoolean())) {
								state.right();
							}

							state.left();
						}

						if (x == 0 || x + 1 == roomWidth) {

							if (y != 0) {
								state.left();
							}

							if (y + 1 != roomHeight) {
								state.right();
							}

						}

						if (y == 0 || y + 1 == roomHeight) {

							if (x != 0) {
								state.up();
							}

							if (x + 1 != roomWidth) {
								state.down();
							}

						}

						if (!(x == 0 || y == 0 || x + 1 == roomWidth || y + 1 == roomHeight)) {

							for (Face face : Face.values()) {
								state.go(face);
							}

						}

					}

				}

			}

		}

		return maze;
	}

	@Override
	public int getPlacementRadius() {
		return 2;
	}

	@Override
	protected Identifier getContainerLootTable(LootableContainerBlockEntity container) {
		return container.getCachedState().isOf(Blocks.CHEST) ? LootTables.WOODLAND_MANSION_CHEST
				: LootTables.SPAWN_BONUS_CHEST;
	}

	@Override
	public int getWorldHeight() {
		return 128;
	}

	@Override
	public int getSeaLevel() {
		return 0;
	}

	@Override
	public int getMinimumY() {
		return 0;
	}

	@Override
	public void method_40450(List<String> list, RandomState randomState, BlockPos pos) {
	}

	@Override
	public Map<String, MazeGenerator> generators() {
		return ImmutableMap.of("main", mazeGenerator);
	}

}
