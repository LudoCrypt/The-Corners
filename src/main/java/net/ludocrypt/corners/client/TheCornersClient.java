package net.ludocrypt.corners.client;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import com.swordglowsblue.artifice.api.Artifice;

import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import net.fabricmc.api.ClientModInitializer;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.init.CornerShaderRegistry;
import net.ludocrypt.corners.init.CornerWorld;
import net.ludocrypt.corners.mixin.MinecraftClientAccessor;
import net.ludocrypt.corners.packet.ServerToClientPackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TheCornersClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		CornerShaderRegistry.init();
		ServerToClientPackets.manageServerToClientPackets();
		ShaderEffectRenderCallback.EVENT.register(tickDelta -> CornerShaderRegistry.getCurrent(MinecraftClient.getInstance().world.getRegistryKey()).render(tickDelta));
		Artifice.registerAssetPack(TheCorners.id("default"), (pack) -> {
			for (Identifier id : Registry.BLOCK.getIds().stream().filter((id) -> id.getNamespace().equals("corners") && id.getPath().startsWith("debug_")).toList()) {
				pack.addBlockState(id, (state) -> {
					state.variant("", (variant) -> {
						variant.model(new Identifier("block/structure_block_corner"));
					});
				});
			}
		});
		get(CornerWorld.YEARNING_CANAL, CornerWorld.YEARNING_CANAL);
	}

	public static float getTickDelta() {
		MinecraftClient client = MinecraftClient.getInstance();
		return client.isPaused() ? ((MinecraftClientAccessor) client).getPausedTickDelta() : client.getTickDelta();
	}

}
