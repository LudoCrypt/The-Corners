package net.ludocrypt.corners.block.entity;

import java.util.List;

import com.google.common.collect.Lists;

import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.mixin.BlockEntityAccessor;
import net.ludocrypt.limlib.impl.access.StructureBlockBlockEntityAccess;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;

public class MazeStructureBlockBlockEntity extends StructureBlockBlockEntity {

	private List<Byte> connectionsUp = Lists.newArrayList();
	private List<Byte> connectionsDown = Lists.newArrayList();
	private List<Byte> connectionsLeft = Lists.newArrayList();
	private List<Byte> connectionsRight = Lists.newArrayList();

	public MazeStructureBlockBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
		((BlockEntityAccessor) this).setType(CornerBlocks.MAZE_STRUCTURE_BLOCK_ENTITY_TYPE);
	}

	public List<Byte> getConnectionsUp() {
		return connectionsUp;
	}

	public List<Byte> getConnectionsDown() {
		return connectionsDown;
	}

	public List<Byte> getConnectionsLeft() {
		return connectionsLeft;
	}

	public List<Byte> getConnectionsRight() {
		return connectionsRight;
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {

		if (((StructureBlockBlockEntityAccess) this).getTags() == null) {
			((StructureBlockBlockEntityAccess) this).setTags(new NbtCompound());
		}

		((StructureBlockBlockEntityAccess) this).getTags().put("connections", this.writeListToNbt());
		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);

		if (((StructureBlockBlockEntityAccess) this).getTags() != null) {
			this.readFromNbtList(((StructureBlockBlockEntityAccess) this).getTags().getCompound("connections"));
		}

	}

	public NbtCompound writeListToNbt() {
		NbtCompound connectionsTag = new NbtCompound();
		NbtList upList = new NbtList();

		for (int i = 0; i < getConnectionsUp().size(); i++) {
			upList.add(NbtByte.of(getConnectionsUp().get(i)));
		}

		NbtList downList = new NbtList();

		for (int i = 0; i < getConnectionsDown().size(); i++) {
			downList.add(NbtByte.of(getConnectionsDown().get(i)));
		}

		NbtList leftList = new NbtList();

		for (int i = 0; i < getConnectionsLeft().size(); i++) {
			leftList.add(NbtByte.of(getConnectionsLeft().get(i)));
		}

		NbtList rightList = new NbtList();

		for (int i = 0; i < getConnectionsRight().size(); i++) {
			rightList.add(NbtByte.of(getConnectionsRight().get(i)));
		}

		connectionsTag.put("up", upList);
		connectionsTag.put("down", downList);
		connectionsTag.put("left", leftList);
		connectionsTag.put("right", rightList);

		return connectionsTag;
	}

	public void readFromNbtList(NbtCompound tag) {

		if (tag.contains("up") && tag.contains("down") && tag.contains("left") && tag.contains("right")) {
			NbtList upList = tag.getList("up", NbtElement.BYTE_TYPE);
			NbtList downList = tag.getList("down", NbtElement.BYTE_TYPE);
			NbtList leftList = tag.getList("left", NbtElement.BYTE_TYPE);
			NbtList rightList = tag.getList("right", NbtElement.BYTE_TYPE);

			this.connectionsUp.clear();

			for (int i = 0; i < upList.size(); i++) {
				this.connectionsUp.add(((NbtByte) upList.get(i)).byteValue());
			}

			this.connectionsDown.clear();

			for (int i = 0; i < downList.size(); i++) {
				this.connectionsDown.add(((NbtByte) downList.get(i)).byteValue());
			}

			this.connectionsLeft.clear();

			for (int i = 0; i < leftList.size(); i++) {
				this.connectionsLeft.add(((NbtByte) leftList.get(i)).byteValue());
			}

			this.connectionsRight.clear();

			for (int i = 0; i < rightList.size(); i++) {
				this.connectionsRight.add(((NbtByte) rightList.get(i)).byteValue());
			}

		}

	}

}
