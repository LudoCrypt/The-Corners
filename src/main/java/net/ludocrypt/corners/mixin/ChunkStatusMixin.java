package net.ludocrypt.corners.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Either;

import net.ludocrypt.corners.world.chunk.ChunkGeneratorExtraInfo;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {

	@Inject(method = "method_33732(Lnet/minecraft/world/chunk/ChunkStatus;Ljava/util/concurrent/Executor;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureManager;Lnet/minecraft/server/world/ServerLightingProvider;Ljava/util/function/Function;Ljava/util/List;Lnet/minecraft/world/chunk/Chunk;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true)
	private static void corners$method_33732$giveChunkGeneratorExtraInfo(ChunkStatus status, Executor executor, ServerWorld world, ChunkGenerator chunkGenerator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> list, Chunk chunk, CallbackInfoReturnable<CompletableFuture<?>> ci) {
		if (chunkGenerator instanceof ChunkGeneratorExtraInfo cgei) {
			ci.setReturnValue(cgei.populateNoise(executor, world.getStructureAccessor(), chunk, status, world, chunkGenerator, structureManager, lightingProvider, function, list).thenApply((chunkx) -> ChunkStatusAccessor.method_33731(status, chunkx)));
		}
	}

}
