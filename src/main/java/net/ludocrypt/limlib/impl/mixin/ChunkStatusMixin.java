package net.ludocrypt.limlib.impl.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Either;

import net.ludocrypt.limlib.api.world.chunk.LiminalChunkGenerator;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.BelowZeroRetrogen;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {

	@Inject(method = "method_38284(Lnet/minecraft/world/chunk/ChunkStatus;Ljava/util/concurrent/Executor;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureTemplateManager;Lnet/minecraft/server/world/ServerLightingProvider;Ljava/util/function/Function;Ljava/util/List;Lnet/minecraft/world/chunk/Chunk;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true)
	private static void limlib$liminalChunkGenerator(ChunkStatus chunkStatus, Executor executor, ServerWorld serverWorld,
			ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager,
			ServerLightingProvider serverLightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> chunks,
			Chunk chunk, CallbackInfoReturnable<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> ci) {

		if (chunkGenerator instanceof LiminalChunkGenerator limChunkGen) {
			ChunkRegion chunkRegion = new ChunkRegion(serverWorld, chunks, chunkStatus, limChunkGen.getPlacementRadius());
			ci
				.setReturnValue(limChunkGen
					.populateNoise(chunkRegion, chunkStatus, executor, serverWorld, chunkGenerator, structureTemplateManager,
						serverLightingProvider, function, chunks, chunk)
					.thenApply(chunkx -> {

						if (chunkx instanceof ProtoChunk protoChunk) {
							BelowZeroRetrogen belowZeroRetrogen = protoChunk.getBelowZeroRetrogen();

							if (belowZeroRetrogen != null) {
								BelowZeroRetrogen.replaceOldBedrock(protoChunk);

								if (belowZeroRetrogen.hasBedrockHoles()) {
									belowZeroRetrogen.applyBedrockMask(protoChunk);
								}

							}

						}

						return Either.left(chunkx);
					}));
		}

	}

}
