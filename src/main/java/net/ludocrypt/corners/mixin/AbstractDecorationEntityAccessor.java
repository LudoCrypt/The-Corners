package net.ludocrypt.corners.mixin;

import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractDecorationEntity.class)
public interface AbstractDecorationEntityAccessor {

	@Invoker
	void callSetFacing(Direction facing);

}
