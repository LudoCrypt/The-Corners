package net.ludocrypt.corners.advancements;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AdvancementHelper {

	public static void grantAdvancement(PlayerEntity player, Identifier id) {

		if (player instanceof ServerPlayerEntity serverPlayerEntity) {
			Advancement advancement = serverPlayerEntity.server.getAdvancementLoader().get(id);
			AdvancementProgress progress = serverPlayerEntity.getAdvancementTracker().getProgress(advancement);

			if (!progress.isDone()) {
				progress
					.getUnobtainedCriteria()
					.forEach((criteria) -> serverPlayerEntity.getAdvancementTracker().grantCriterion(advancement, criteria));
			}

		}

	}

}
