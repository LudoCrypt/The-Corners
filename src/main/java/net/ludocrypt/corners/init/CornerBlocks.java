package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.ludocrypt.corners.block.CornerStairsBlock;
import net.ludocrypt.corners.block.RadioBlock;
import net.ludocrypt.corners.block.RailingBlock;
import net.ludocrypt.corners.block.SkyboxGlassBlock;
import net.ludocrypt.corners.block.SkyboxGlassPaneBlock;
import net.ludocrypt.corners.block.ThinPillarBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.ItemGroup;

public class CornerBlocks {

	public static final Block STONE_PILLAR = get("stone_pillar", new ThinPillarBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS)), ItemGroup.DECORATIONS);
	public static final Block WOODEN_RADIO = get("wooden_radio", new RadioBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD)), ItemGroup.REDSTONE);
	public static final Block TUNED_RADIO = get("tuned_radio", new RadioBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD)), ItemGroup.REDSTONE);

	public static final Block DRYWALL = get("drywall", new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)), ItemGroup.BUILDING_BLOCKS);

	public static final Block NYLON_FIBER_BLOCK = get("nylon_fiber_block", new Block(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)), ItemGroup.BUILDING_BLOCKS);
	public static final Block NYLON_FIBER_STAIRS = get("nylon_fiber_stairs", new CornerStairsBlock(NYLON_FIBER_BLOCK.getDefaultState(), FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)), ItemGroup.BUILDING_BLOCKS);
	public static final Block NYLON_FIBER_SLAB = get("nylon_fiber_slab", new SlabBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)), ItemGroup.BUILDING_BLOCKS);

	public static final Block SNOWY_GLASS = get("snowy_glass", new SkyboxGlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS)), ItemGroup.DECORATIONS);
	public static final Block SNOWY_GLASS_PANE = get("snowy_glass_pane", new SkyboxGlassPaneBlock(FabricBlockSettings.copyOf(Blocks.GLASS_PANE)), ItemGroup.DECORATIONS);

	public static final Block DARK_RAILING = get("dark_railing", new RailingBlock(FabricBlockSettings.copyOf(Blocks.STONE)), ItemGroup.DECORATIONS);

	public static void init() {
		FlammableBlockRegistry registry = FlammableBlockRegistry.getDefaultInstance();
		registry.add(NYLON_FIBER_BLOCK, 30, 60);
		registry.add(NYLON_FIBER_STAIRS, 30, 60);
		registry.add(NYLON_FIBER_SLAB, 30, 60);
		registry.add(DRYWALL, 5, 20);
		registry.add(WOODEN_RADIO, 10, 20);
	}

}
