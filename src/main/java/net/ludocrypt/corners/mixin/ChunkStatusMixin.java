package net.ludocrypt.corners.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Either;

import net.ludocrypt.corners.world.chunk.ChunkGeneratorExtraInfo;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {

	@Group(name = "status", min = 1)
	@Inject(method = "method_33732(Lnet/minecraft/world/chunk/ChunkStatus;Ljava/util/concurrent/Executor;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureManager;Lnet/minecraft/server/world/ServerLightingProvider;Ljava/util/function/Function;Ljava/util/List;Lnet/minecraft/world/chunk/Chunk;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true, remap = false, allow = 1)
	private static void corners$method_33732$giveChunkGeneratorExtraInfo$mapped(ChunkStatus status, Executor executor, ServerWorld world, ChunkGenerator chunkGenerator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> list, Chunk chunk, CallbackInfoReturnable<CompletableFuture<?>> ci) {
		corners$method_33732$giveChunkGeneratorExtraInfo(status, executor, world, chunkGenerator, structureManager, lightingProvider, function, list, chunk, ci);
	}

	@Group(name = "status", min = 1)
	@Inject(method = "method_33732(Lnet/minecraft/class_2806;Ljava/util/concurrent/Executor;Lnet/minecraft/class_3218;Lnet/minecraft/class_2794;Lnet/minecraft/class_3485;Lnet/minecraft/class_3227;Ljava/util/function/Function;Ljava/util/List;Lnet/minecraft/class_2791;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true, remap = false, allow = 1)
	private static void corners$method_33732$giveChunkGeneratorExtraInfo$unmapped(ChunkStatus status, Executor executor, ServerWorld world, ChunkGenerator chunkGenerator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> list, Chunk chunk, CallbackInfoReturnable<CompletableFuture<?>> ci) {
		corners$method_33732$giveChunkGeneratorExtraInfo(status, executor, world, chunkGenerator, structureManager, lightingProvider, function, list, chunk, ci);
	}

	private static void corners$method_33732$giveChunkGeneratorExtraInfo(ChunkStatus status, Executor executor, ServerWorld world, ChunkGenerator chunkGenerator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> list, Chunk chunk, CallbackInfoReturnable<CompletableFuture<?>> ci) {
		if (chunkGenerator instanceof ChunkGeneratorExtraInfo cgei) {
			ChunkRegion chunkRegion = new ChunkRegion(world, list, status, cgei.getPlacementRadius());
			ci.setReturnValue(cgei.populateNoise(executor, world.getStructureAccessor().forRegion(chunkRegion), chunk, status, world, chunkRegion, chunkGenerator, structureManager, lightingProvider, function, list).thenApply((chunkx) -> {
				if (chunkx instanceof ProtoChunk proto) {
					proto.setStatus(status);
				}
				return Either.left(chunkx);
			}));
		}
	}

}
