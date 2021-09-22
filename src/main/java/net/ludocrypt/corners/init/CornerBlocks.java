package net.ludocrypt.corners.init;

import static net.ludocrypt.corners.util.RegistryHelper.get;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.ludocrypt.corners.block.CornerStairsBlock;
import net.ludocrypt.corners.block.DebugPaintingSpawnerBlock;
import net.ludocrypt.corners.block.RadioBlock;
import net.ludocrypt.corners.block.SkyboxBlock;
import net.ludocrypt.corners.block.ThinPillarBlock;
import net.ludocrypt.corners.block.entity.SkyboxBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CornerBlocks {

	public static final Block STONE_PILLAR = get("stone_pillar", new ThinPillarBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS).breakByTool(FabricToolTags.PICKAXES)), ItemGroup.DECORATIONS);
	public static final Block WOODEN_RADIO = get("wooden_radio", new RadioBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).breakByTool(FabricToolTags.AXES)), ItemGroup.REDSTONE);

	public static final Block SKYBOX_BLOCK = get("skybox_block", new SkyboxBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK).air()));
	public static final BlockEntityType<SkyboxBlockEntity> SKYBOX_BLOCK_ENTITY = get("skybox_block_entity_type", FabricBlockEntityTypeBuilder.create(SkyboxBlockEntity::new, SKYBOX_BLOCK));

	public static final Block NYLON_FIBER_BLOCK = get("nylon_fiber_block", new Block(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).breakByTool(FabricToolTags.SHEARS)), ItemGroup.DECORATIONS);
	public static final Block NYLON_FIBER_STAIRS = get("nylon_fiber_stairs", new CornerStairsBlock(NYLON_FIBER_BLOCK.getDefaultState(), FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).breakByTool(FabricToolTags.SHEARS)), ItemGroup.DECORATIONS);
	public static final Block NYLON_FIBER_SLAB = get("nylon_fiber_slab", new SlabBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).breakByTool(FabricToolTags.SHEARS)), ItemGroup.DECORATIONS);

	public static final Block DRYWALL = get("drywall", new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).breakByTool(FabricToolTags.AXES)), ItemGroup.DECORATIONS);

	// Debug
	public static final Block DEBUG_AIR_BLOCK = get("debug_air_block", new Block(FabricBlockSettings.copyOf(Blocks.STONE)));

	public static void init() {
		for (Identifier id : Registry.PAINTING_MOTIVE.getIds()) {
			get("debug_" + id.getNamespace() + "_" + id.getPath() + "_painting_spawner", new DebugPaintingSpawnerBlock(Registry.PAINTING_MOTIVE.get(id), FabricBlockSettings.copyOf(Blocks.STONE)));
		}
	}

}
