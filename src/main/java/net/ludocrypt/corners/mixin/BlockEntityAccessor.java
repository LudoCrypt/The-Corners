package net.ludocrypt.corners.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockEntity.class)
public interface BlockEntityAccessor {

	@Accessor
	BlockEntityType<?> getType();

	@Mutable
	@Accessor
	void setType(BlockEntityType<?> type);

}
