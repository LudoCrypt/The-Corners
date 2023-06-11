package net.ludocrypt.corners.packet;

import java.util.Comparator;
import java.util.List;

import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import net.ludocrypt.corners.access.MusicTrackerAccess;
import net.ludocrypt.corners.client.sound.LoopingPositionedSoundInstance;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerRadioRegistry;
import net.ludocrypt.corners.mixin.SoundManagerAccessor;
import net.ludocrypt.corners.util.DimensionalPaintingVariant;
import net.ludocrypt.corners.util.RadioSoundTable;
import net.ludocrypt.limlib.impl.access.SoundSystemAccess;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;

public class ServerToClientPackets {

	public static void manageServerToClientPackets() {
		ClientPlayNetworking.registerGlobalReceiver(ClientToServerPackets.PLAY_RADIO, (client, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			boolean start = buf.readBoolean();

			client.execute(() -> {
				RadioSoundTable id = CornerRadioRegistry.getCurrent(client);

				List<PaintingEntity> closestPaintings = client.world
						.getEntitiesByClass(PaintingEntity.class, Box.from(Vec3d.of(pos)).expand(16.0D), (entity) -> entity.getVariant().value() instanceof DimensionalPaintingVariant).stream()
						.sorted(Comparator.comparing((entity) -> entity.squaredDistanceTo(Vec3d.of(pos)))).toList();
				if (!closestPaintings.isEmpty()) {
					id = CornerRadioRegistry.getCurrent(((DimensionalPaintingVariant) closestPaintings.get(0).getVariant().value()).radioRedirect);
				}

				SoundSystemAccess.get(((SoundManagerAccessor) client.getSoundManager()).getSoundSystem()).stopSoundsAtPosition(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, null,
						SoundCategory.RECORDS);
				((MusicTrackerAccess) (client.getMusicTracker())).getRadioPositions().remove(pos);
				if (start) {
					((MusicTrackerAccess) (client.getMusicTracker())).getRadioPositions().add(pos);

					SoundEvent soundEvent = id.getRadioSound().value();

					if (client.world.getBlockState(pos).isOf(CornerBlocks.WOODEN_RADIO)) {
						soundEvent = id.getRadioSound().value();
					} else if (client.world.getBlockState(pos).isOf(CornerBlocks.TUNED_RADIO)) {
						soundEvent = id.getMusicSound().value();
					} else if (client.world.getBlockState(pos).isOf(CornerBlocks.BROKEN_RADIO)) {
						soundEvent = id.getStaticSound().value();
					}

					LoopingPositionedSoundInstance.play(client.world, pos, soundEvent, SoundCategory.RECORDS, 1.0F, 1.0F, RandomGenerator.createLegacy(), pos.getX() + 0.5, pos.getY() + 1.0,
							pos.getZ() + 0.5);
				}
			});
		});
	}

}
