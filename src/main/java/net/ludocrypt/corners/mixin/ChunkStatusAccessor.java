package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.datafixers.util.Either;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

@Mixin(ChunkStatus.class)
public interface ChunkStatusAccessor {

	@Invoker("method_33731")
	public static Either<?, ?> method_33731(ChunkStatus status, Chunk chunk) {
		throw new AssertionError();
	}

}
