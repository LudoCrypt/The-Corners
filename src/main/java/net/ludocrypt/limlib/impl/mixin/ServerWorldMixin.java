package net.ludocrypt.limlib.impl.mixin;

import java.util.List;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.storage.MazeStorage;
import net.ludocrypt.limlib.api.world.maze.storage.MazeStorageProvider;
import net.ludocrypt.limlib.api.world.maze.storage.ServerWorldMazeAccess;
import net.ludocrypt.limlib.api.world.nbt.NbtTags;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.unmapped.C_xmjhbbku;
import net.minecraft.world.ServerWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.storage.WorldSaveStorage;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ServerWorldMazeAccess {

	@Unique
	MazeStorage mazeStorage;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void limlib$init(MinecraftServer server, Executor executor, WorldSaveStorage.Session session,
			ServerWorldProperties worldProperties, RegistryKey<World> registryKey, DimensionOptions dimensionOptions,
			WorldGenerationProgressListener worldGenerationProgressListener, boolean bl, long l, List<Spawner> spawners,
			boolean shouldTickTime, @Nullable C_xmjhbbku c_xmjhbbku, CallbackInfo ci) {

		if (dimensionOptions.getChunkGenerator() instanceof MazeStorageProvider provider) {
			this.mazeStorage = new MazeStorage(provider.generators(),
				session.getWorldDirectory(registryKey).resolve("maze_region").toFile());
		}

		if (dimensionOptions.getChunkGenerator() instanceof AbstractNbtChunkGenerator generator) {

			if (generator.tags == null) {
				generator.tags = NbtTags.parse(generator.nbtGroup, server.getResourceManager());
			}

			generator.loadTags();
		}

	}

	@Inject(method = "saveWorld", at = @At("TAIL"))
	private void limlib$saveWorld(CallbackInfo ci) {

		if (this.mazeStorage != null) {
			this.mazeStorage.save();
		}

	}

	@Override
	public MazeStorage getMazeStorage() {
		return this.mazeStorage;
	}

}
