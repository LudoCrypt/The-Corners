package net.ludocrypt.corners.block.entity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.init.CornerBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class SkyboxBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

	public Identifier skyboxId = TheCorners.id("textures/sky/office");

	public SkyboxBlockEntity(BlockPos pos, BlockState state) {
		super(CornerBlocks.SKYBOX_BLOCK_ENTITY, pos, state);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		skyboxId = new Identifier(nbt.getString("skybox"));
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putString("skybox", skyboxId.toString());
		return super.writeNbt(nbt);
	}

	@Override
	public void fromClientTag(NbtCompound nbt) {
		super.readNbt(nbt);
	}

	@Override
	public NbtCompound toClientTag(NbtCompound nbt) {
		return super.writeNbt(nbt);
	}

}
