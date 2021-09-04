package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import net.ludocrypt.corners.entity.DimensionalPaintingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

	public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Unique
	private int portalId = 1032;

	@Inject(method = "moveToWorld", at = @At("HEAD"))
	private void corners$moveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> ci) {
		if (DimensionalPaintingEntity.isPaingintTeleport) {
			portalId = 29848748;
			DimensionalPaintingEntity.isPaingintTeleport = false;
		} else {
			portalId = 1032;
		}
	}

	@ModifyArg(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/WorldEventS2CPacket;<init>(ILnet/minecraft/util/math/BlockPos;IZ)V", ordinal = 0), index = 0)
	private int corners$moveToWorld(int in) {
		int oldPortalId = portalId;
		portalId = 1032;
		return oldPortalId;
	}

}
