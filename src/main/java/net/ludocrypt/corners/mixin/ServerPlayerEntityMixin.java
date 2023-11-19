package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.advancements.AdvancementHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

	public ServerPlayerEntityMixin(World world, BlockPos pos, float f, GameProfile gameProfile) {
		super(world, pos, f, gameProfile);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void corners$tick(CallbackInfo ci) {

		if (this.getWorld().getRegistryKey().getValue().getNamespace().equals("corners")) {
			AdvancementHelper.grantAdvancement(this, TheCorners.id("root"));
		}

	}

}
