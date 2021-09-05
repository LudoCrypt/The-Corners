package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.ludocrypt.corners.block.DebugPaintingSpawnerBlock;
import net.ludocrypt.corners.block.ThinPillarBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CornerBlocks {

	public static final Block STONE_PILLAR = get("stone_pillar", new ThinPillarBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS).breakByTool(FabricToolTags.PICKAXES)), ItemGroup.BUILDING_BLOCKS);
	public static final Block DEBUG_AIR_BLOCK = get("debug_air_block", new Block(FabricBlockSettings.copyOf(Blocks.STONE)));

	public static void init() {
		for (Identifier id : Registry.PAINTING_MOTIVE.getIds()) {
			get("debug_" + id.getNamespace() + "_" + id.getPath() + "_painting_spawner", new DebugPaintingSpawnerBlock(Registry.PAINTING_MOTIVE.get(id), FabricBlockSettings.copyOf(Blocks.STONE)));
		}
	}

}
