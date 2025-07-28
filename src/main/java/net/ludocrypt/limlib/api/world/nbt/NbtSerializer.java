package net.ludocrypt.limlib.api.world.nbt;

import net.minecraft.nbt.NbtCompound;

public interface NbtSerializer<T> {

	public NbtCompound write();

	public T read(NbtCompound nbt);

}
