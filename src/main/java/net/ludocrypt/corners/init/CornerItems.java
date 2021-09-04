package net.ludocrypt.corners.init;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.ludocrypt.corners.TheCorners;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class CornerItems {

	public static void init() {
		FabricItemGroupBuilder.create(TheCorners.id("items")).icon(() -> Blocks.STONE.asItem().getDefaultStack()).appendItems((stacks) -> Registry.ITEM.stream().filter((item) -> Registry.ITEM.getId(item).getNamespace().equals("corners")).forEach((item) -> stacks.add(new ItemStack(item)))).build();
	}

}
