package net.ludocrypt.corners.world.chunk;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.init.CornerWorlds;
import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.MazePiece;
import net.ludocrypt.limlib.api.world.nbt.NbtGroup;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class HoaryCrossroadsChunkGenerator extends AbstractNbtChunkGenerator {

	public static final Codec<HoaryCrossroadsChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.populationSource;
		}), NbtGroup.CODEC.fieldOf("group").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.nbtGroup;
		}), Codec.INT.fieldOf("maze_width").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeWidth;
		}), Codec.INT.fieldOf("maze_height").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeHeight;
		}), Codec.INT.fieldOf("maze_dilation").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeDilation;
		}), Codec.LONG.fieldOf("seed_modifier").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeSeedModifier;
		})).apply(instance, instance.stable(HoaryCrossroadsChunkGenerator::new));
	});

	private int mazeWidth;
	private int mazeHeight;
	private int mazeDilation;
	private long mazeSeedModifier;

	public HoaryCrossroadsChunkGenerator(BiomeSource biomeSource, NbtGroup group, int mazeWidth, int mazeHeight,
			int mazeDilation, long mazeSeedModifier) {
		super(biomeSource, group);
		this.mazeWidth = mazeWidth;
		this.mazeHeight = mazeHeight;
		this.mazeDilation = mazeDilation;
		this.mazeSeedModifier = mazeSeedModifier;
	}

	public static NbtGroup createGroup() {
		return NbtGroup.Builder
			.create(TheCorners.id(CornerWorlds.HOARY_CROSSROADS))
			.with("hoary_crossroads_f_decorated", 1, 7)
			.with("hoary_crossroads_f_bottom")
			.with("hoary_crossroads_f_rare")
			.with("hoary_crossroads_f")
			.with("hoary_crossroads_i_decorated", 1, 9)
			.with("hoary_crossroads_i_bottom")
			.with("hoary_crossroads_i_rare")
			.with("hoary_crossroads_i")
			.with("hoary_crossroads_l_decorated", 1, 9)
			.with("hoary_crossroads_l_bottom")
			.with("hoary_crossroads_l_rare")
			.with("hoary_crossroads_l")
			.with("hoary_crossroads_nub_decorated", 1, 6)
			.with("hoary_crossroads_nub_bottom")
			.with("hoary_crossroads_nub_rare")
			.with("hoary_crossroads_nub")
			.with("hoary_crossroads_t_decorated", 1, 7)
			.with("hoary_crossroads_t_bottom")
			.with("hoary_crossroads_t_rare", 1, 1)
			.with("hoary_crossroads_t")
			.with("hoary_crossroads_obelisk", 1, 4)
			.build();
	}

	@Override
	public void loadTags() {
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
		return CompletableFuture.completedFuture(chunk);
	}

	public Identifier getPiece(MazePiece piece, RandomGenerator random) {
		String group = switch (piece) {
			case F -> "hoary_crossroads_f";
			case I -> "hoary_crossroads_i";
			case L -> "hoary_crossroads_l";
			case T -> "hoary_crossroads_t";
			default -> "hoary_crossroads_nub";
		};

		if ((random.nextInt(8) == 0)) {

			if ((random.nextInt(13) == 0)) {
				group += "_rare";
			} else {
				group += "_decorated";
			}

		}

		return nbtGroup.pick(random, group);
	}

	public Identifier getPieceAsBottom(MazePiece piece, RandomGenerator random) {

		switch (piece) {
			case F:
				return nbtGroup.pick(random, "hoary_crossroads_f_bottom");
			case I:
				return nbtGroup.pick(random, "hoary_crossroads_i_bottom");
			case L:
				return nbtGroup.pick(random, "hoary_crossroads_l_bottom");
			case N:
				return nbtGroup.pick(random, "hoary_crossroads_nub_bottom");
			case T:
				return nbtGroup.pick(random, "hoary_crossroads_t_bottom");
			default:
				return nbtGroup.pick(random, "hoary_crossroads_nub_bottom");
		}

	}

	@Override
	public int getPlacementRadius() {
		return 1;
	}

	@Override
	protected Identifier getContainerLootTable(LootableContainerBlockEntity container) {
		return LootTables.SHIPWRECK_SUPPLY_CHEST;
	}

	@Override
	public int getWorldHeight() {
		return 512;
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

}
