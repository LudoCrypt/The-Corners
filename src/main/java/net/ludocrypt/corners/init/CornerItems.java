package net.ludocrypt.corners.init;

import org.quiltmc.qsl.item.group.api.QuiltItemGroup;

import net.ludocrypt.corners.TheCorners;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class CornerItems {

	public static void init() {
		QuiltItemGroup.builder(TheCorners.id("items")).icon(() -> CornerBlocks.WOODEN_RADIO.asItem().getDefaultStack()).appendItems((stacks) -> Registry.ITEM.stream().filter((item) -> Registry.ITEM.getId(item).getNamespace().equals("corners")).forEach((item) -> stacks.add(new ItemStack(item)))).build();
	}

}
