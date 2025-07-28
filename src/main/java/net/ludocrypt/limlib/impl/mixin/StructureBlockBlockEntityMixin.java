package net.ludocrypt.limlib.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.ludocrypt.limlib.impl.access.StructureBlockBlockEntityAccess;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.BlockPos;

@Mixin(StructureBlockBlockEntity.class)
public class StructureBlockBlockEntityMixin implements StructureBlockBlockEntityAccess {

	@Unique
	NbtCompound tags;

	@Inject(method = "writeNbt", at = @At("TAIL"))
	protected void limlib$writeNbt(NbtCompound nbt, CallbackInfo ci) {

		if (tags != null) {
			nbt.put("limlib_tag", tags);
		}

	}

	@Inject(method = "readNbt", at = @At("TAIL"))
	protected void limlib$readNbt(NbtCompound nbt, CallbackInfo ci) {

		if (nbt.contains("limlib_tag")) {
			this.tags = nbt.getCompound("limlib_tag");
		}

	}

	@Inject(method = "Lnet/minecraft/block/entity/StructureBlockBlockEntity;saveStructure(Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/Structure;setAuthor(Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	protected void limlib$saveStructure(boolean bl, CallbackInfoReturnable<Boolean> ci, BlockPos blockPos,
			ServerWorld serverWorld, StructureTemplateManager structureTemplateManager, Structure structure) {

		if (tags != null) {
			((StructureBlockBlockEntityAccess) structure).setTags(tags);
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
