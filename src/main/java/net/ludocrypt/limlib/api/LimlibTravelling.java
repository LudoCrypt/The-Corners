package net.ludocrypt.limlib.api;

import java.util.Set;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.quiltmc.qsl.worldgen.dimension.api.QuiltDimensions;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class LimlibTravelling {

	@Internal
	public static SoundEvent travelingSound = null;

	@Internal
	public static float travelingVolume = 0.25F;

	@Internal
	public static float travelingPitch = 1.0F;

	public static <E extends Entity> E travelTo(E teleported, ServerWorld destination, TeleportTarget target,
			SoundEvent sound, float volume, float pitch) {

		if (destination.equals(teleported.getWorld())) {

			BlockPos blockPos = BlockPos.create(target.position.x, target.position.y, target.position.z);

			if (!World.isValid(blockPos)) {
				throw new UnsupportedOperationException("Position " + blockPos.toString() + " is out of this world!");
			}

			float f = MathHelper.wrapDegrees(target.yaw);
			float g = MathHelper.wrapDegrees(target.pitch);

			if (teleported instanceof ServerPlayerEntity) {
				ChunkPos chunkPos = new ChunkPos(blockPos);
				destination.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, teleported.getId());
				teleported.stopRiding();

				if (((ServerPlayerEntity) teleported).isSleeping()) {
					((ServerPlayerEntity) teleported).wakeUp(true, true);
				}

				((ServerPlayerEntity) teleported).networkHandler
					.requestTeleport(target.position.x, target.position.y, target.position.z, f, g, Set.of());

				teleported.setHeadYaw(f);
			} else {
				float h = MathHelper.clamp(g, -90.0f, 90.0f);
				teleported.refreshPositionAndAngles(target.position.x, target.position.y, target.position.z, f, h);
				teleported.setHeadYaw(f);
			}

			teleported.setVelocity(target.velocity);
			teleported
				.getWorld()
				.playSound(null, teleported.getX(), teleported.getY(), teleported.getZ(), sound, SoundCategory.AMBIENT,
					volume, pitch);

			return teleported;
		} else {

			try {
				travelingSound = sound;
				travelingVolume = volume;
				travelingPitch = pitch;
				return QuiltDimensions.teleport(teleported, destination, target);
			} finally {
				travelingSound = null;
				travelingVolume = 0.0F;
				travelingPitch = 0.0F;
			}

		}

	}

}
