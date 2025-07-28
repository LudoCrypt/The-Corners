package net.ludocrypt.limlib.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.limlib.impl.access.StructureBlockBlockEntityAccess;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.HolderProvider;
import net.minecraft.structure.Structure;

@Mixin(Structure.class)
public class StructureMixin implements StructureBlockBlockEntityAccess {

	@Unique
	NbtCompound tags;

	@Inject(method = "writeNbt", at = @At("TAIL"))
	protected void limlib$writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> ci) {

		if (tags != null) {
			nbt.put("limlib_tag", tags);
		}

	}

	@Inject(method = "readNbt", at = @At("TAIL"))
	protected void limlib$readNbt(HolderProvider<Block> blockProvider, NbtCompound nbt, CallbackInfo ci) {

		if (nbt.contains("limlib_tag")) {
			this.tags = nbt.getCompound("limlib_tag");
		}

	}

	@Override
	public NbtCompound getTags() {
		return tags;
	}

	@Override
	public void setTags(NbtCompound tags) {
		this.tags = tags;
	}

}
