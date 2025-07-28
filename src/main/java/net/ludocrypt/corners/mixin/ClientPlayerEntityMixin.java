package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.corners.block.entity.MazeStructureBlockBlockEntity;
import net.ludocrypt.corners.client.screen.MazeStructureBlockScreen;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

	@Shadow
	@Final
	protected MinecraftClient client;

	@Inject(method = "Lnet/minecraft/client/network/ClientPlayerEntity;openStructureBlockScreen(Lnet/minecraft/block/entity/StructureBlockBlockEntity;)V", at = @At("HEAD"), cancellable = true)
	public void corners$openStructureBlockScreen(StructureBlockBlockEntity structureBlock, CallbackInfo ci) {

		if (structureBlock instanceof MazeStructureBlockBlockEntity msbbe) {
			this.client.setScreen(new MazeStructureBlockScreen(msbbe));
			ci.cancel();
		}

	}

}
