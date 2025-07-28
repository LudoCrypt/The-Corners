package net.ludocrypt.limlib.impl.access;

import net.minecraft.nbt.NbtCompound;

public interface StructureBlockBlockEntityAccess {

	public NbtCompound getTags();

	public void setTags(NbtCompound tags);

}
