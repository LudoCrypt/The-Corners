package net.ludocrypt.corners.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.access.SoundSystemAccess;
import net.ludocrypt.corners.client.sound.LoopingPositionedSoundInstance;
import net.ludocrypt.corners.init.CornerRadioRegistry;
import net.ludocrypt.corners.init.CornerSoundEvents;
import net.ludocrypt.corners.mixin.SoundManagerAccessor;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class CornersPacketManager {

	public static final Identifier PLAY_RADIO = TheCorners.id("play_radio");

	public static void manageServerTopClientPackets() {
		ClientPlayNetworking.registerGlobalReceiver(CornersPacketManager.PLAY_RADIO, (client, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			boolean start = buf.readBoolean();

			client.execute(() -> {
				SoundEvent id = CornerRadioRegistry.getCurrent(client);
				SoundEvent underlyingId = CornerSoundEvents.UNDERLYING_STATIC;

				SoundSystemAccess.get(((SoundManagerAccessor) client.getSoundManager()).getSoundSystem()).stopSoundsAtPosition(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, id.getId(), SoundCategory.RECORDS);
				SoundSystemAccess.get(((SoundManagerAccessor) client.getSoundManager()).getSoundSystem()).stopSoundsAtPosition(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, underlyingId.getId(), SoundCategory.RECORDS);

				if (start) {
					LoopingPositionedSoundInstance.play(client.world, pos, id, SoundCategory.RECORDS, 1.0F, 1.0F, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
					LoopingPositionedSoundInstance.playNormalLoop(client.world, pos, underlyingId, SoundCategory.RECORDS, 1.0F, 1.0F, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
				}
			});
		});
	}

	public static void manageClientToServerPackets() {

	}

}
