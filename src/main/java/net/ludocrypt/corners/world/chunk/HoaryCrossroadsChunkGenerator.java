package net.ludocrypt.corners.world.chunk;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.limlib.api.LiminalUtil;
import net.ludocrypt.limlib.api.world.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.ludocrypt.limlib.api.world.maze.MazeGenerator;
import net.minecraft.loot.LootTables;
import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class HoaryCrossroadsChunkGenerator extends AbstractNbtChunkGenerator {

	public static final Codec<HoaryCrossroadsChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.worldSeed;
		}), MazeGenerator.CODEC.fieldOf("maze_generator").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeGenerator;
		})).apply(instance, instance.stable(HoaryCrossroadsChunkGenerator::new));
	});

	private long worldSeed;
	private MazeGenerator mazeGenerator;

	public HoaryCrossroadsChunkGenerator(BiomeSource biomeSource, long worldSeed, MazeGenerator<? extends ChunkGenerator, ? extends MazeComponent> mazeGenerator) {
		super(new SimpleRegistry<StructureSet>(Registry.STRUCTURE_SET_KEY, Lifecycle.stable(), null), Optional.empty(), biomeSource, biomeSource, worldSeed, TheCorners.id("hoary_crossroads"), LiminalUtil.createMultiNoiseSampler());
		this.mazeGenerator = mazeGenerator;
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return new HoaryCrossroadsChunkGenerator(this.biomeSource, seed, this.mazeGenerator);
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, Unloaded>>> function, List<Chunk> chunks, Chunk chunk, boolean bl) {
		BlockPos startPos = chunk.getPos().getStartPos();
		this.mazeGenerator.generateMaze(startPos, chunk, region, this);

		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public void storeStructures(ServerWorld world) {
		store("hoary_crossroads_decorated_f", world, 1, 4);
		store("hoary_crossroads_decorated_i", world, 1, 4);
		store("hoary_crossroads_decorated_l", world, 1, 4);
		store("hoary_crossroads_decorated_t", world, 1, 5);
		store("hoary_crossroads_f", world);
		store("hoary_crossroads_i", world);
		store("hoary_crossroads_l", world);
		store("hoary_crossroads_t", world);
		store("hoary_crossroads_nub", world);
		store("hoary_crossroads_f_bottom", world);
		store("hoary_crossroads_i_bottom", world);
		store("hoary_crossroads_l_bottom", world);
		store("hoary_crossroads_t_bottom", world);
		store("hoary_crossroads_nub_bottom", world);
	}

	@Override
	public int chunkRadius() {
		return 1;
	}

	@Override
	protected Identifier getBarrelLootTable() {
		return LootTables.IGLOO_CHEST_CHEST;
	}

	@Override
	public int getWorldHeight() {
		return 512;
	}

	@Override
	public int getHeight(int x, int y, Heightmap.Type type, HeightLimitView world) {
		return world.getTopY();
	}
}
