package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.ludocrypt.corners.block.ThinPillarBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;

public class CornerBlocks {

	public static final Block STONE_PILLAR = get("stone_pillar", new ThinPillarBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS).breakByTool(FabricToolTags.PICKAXES)), ItemGroup.BUILDING_BLOCKS);

	public static void init() {

	}

}
