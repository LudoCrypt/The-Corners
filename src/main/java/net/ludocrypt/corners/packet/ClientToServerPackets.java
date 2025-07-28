package net.ludocrypt.corners.packet;

import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.block.entity.MazeStructureBlockBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ClientToServerPackets {

	public static final Identifier MAZE_STRUCTURE_BLOCK = TheCorners.id("structure_block");

	public static void manageClientToServerPackets() {
		ServerPlayNetworking.registerGlobalReceiver(MAZE_STRUCTURE_BLOCK, (server, player, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			NbtCompound connectionsTag = buf.readNbt();

			server.execute(() -> {

				if (player.getWorld().getBlockEntity(pos) instanceof MazeStructureBlockBlockEntity msbbe) {
					msbbe.readFromNbtList(connectionsTag);
				}

			});
		});
	}

}
