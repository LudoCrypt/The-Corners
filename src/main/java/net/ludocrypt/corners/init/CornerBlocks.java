package net.ludocrypt.corners.init;

import java.util.Map;
import java.util.Set;

import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.quiltmc.qsl.block.content.registry.api.FlammableBlockEntry;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.block.CornerStairsBlock;
import net.ludocrypt.corners.block.RadioBlock;
import net.ludocrypt.corners.block.RailingBlock;
import net.ludocrypt.corners.block.SkyboxGlassBlock;
import net.ludocrypt.corners.block.SkyboxGlassPaneBlock;
import net.ludocrypt.corners.block.SkyboxGlassSlabBlock;
import net.ludocrypt.corners.block.ThinPillarBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class CornerBlocks {

	private static final Map<RegistryKey<ItemGroup>, Set<Item>> CORNERS_ITEM_GROUP_ENTRIES = Maps.newHashMap();

	public static final Block STONE_PILLAR = get("stone_pillar", new ThinPillarBlock(QuiltBlockSettings.copyOf(Blocks.STONE_BRICKS)), ItemGroups.NATURAL_BLOCKS);

	public static final Block WOODEN_RADIO = get("wooden_radio", new RadioBlock(QuiltBlockSettings.copyOf(Blocks.OAK_WOOD)), ItemGroups.REDSTONE_BLOCKS);
	public static final Block TUNED_RADIO = get("tuned_radio", new RadioBlock(QuiltBlockSettings.copyOf(Blocks.OAK_WOOD)), ItemGroups.REDSTONE_BLOCKS);
	public static final Block BROKEN_RADIO = get("broken_radio", new RadioBlock(QuiltBlockSettings.copyOf(Blocks.OAK_WOOD)), ItemGroups.REDSTONE_BLOCKS);

	public static final Block DRYWALL = get("drywall", new Block(QuiltBlockSettings.copyOf(Blocks.OAK_PLANKS)), ItemGroups.BUILDING_BLOCKS);

	public static final Block NYLON_FIBER_BLOCK = get("nylon_fiber_block", new Block(QuiltBlockSettings.copyOf(Blocks.WHITE_WOOL)), ItemGroups.BUILDING_BLOCKS);
	public static final Block NYLON_FIBER_STAIRS = get("nylon_fiber_stairs", new CornerStairsBlock(NYLON_FIBER_BLOCK.getDefaultState(), QuiltBlockSettings.copyOf(Blocks.WHITE_WOOL)),
			ItemGroups.BUILDING_BLOCKS);
	public static final Block NYLON_FIBER_SLAB = get("nylon_fiber_slab", new SlabBlock(QuiltBlockSettings.copyOf(Blocks.WHITE_WOOL)), ItemGroups.BUILDING_BLOCKS);

	public static final Block SNOWY_GLASS = get("snowy_glass", new SkyboxGlassBlock(QuiltBlockSettings.copyOf(Blocks.GLASS)), ItemGroups.BUILDING_BLOCKS);
	public static final Block SNOWY_GLASS_PANE = get("snowy_glass_pane", new SkyboxGlassPaneBlock(QuiltBlockSettings.copyOf(Blocks.GLASS_PANE)), ItemGroups.BUILDING_BLOCKS);
	public static final Block SNOWY_GLASS_SLAB = get("snowy_glass_slab", new SkyboxGlassSlabBlock(QuiltBlockSettings.copyOf(Blocks.GLASS)), ItemGroups.BUILDING_BLOCKS);

	public static final Block DARK_RAILING = get("dark_railing", new RailingBlock(QuiltBlockSettings.copyOf(Blocks.STONE)), ItemGroups.BUILDING_BLOCKS);

	public static void init() {

		BlockContentRegistries.FLAMMABLE.put(NYLON_FIBER_BLOCK, new FlammableBlockEntry(30, 60));
		BlockContentRegistries.FLAMMABLE.put(NYLON_FIBER_STAIRS, new FlammableBlockEntry(30, 60));
		BlockContentRegistries.FLAMMABLE.put(NYLON_FIBER_SLAB, new FlammableBlockEntry(30, 60));
		BlockContentRegistries.FLAMMABLE.put(DRYWALL, new FlammableBlockEntry(5, 20));
		BlockContentRegistries.FLAMMABLE.put(WOODEN_RADIO, new FlammableBlockEntry(10, 20));
		BlockContentRegistries.FLAMMABLE.put(TUNED_RADIO, new FlammableBlockEntry(10, 20));
		BlockContentRegistries.FLAMMABLE.put(BROKEN_RADIO, new FlammableBlockEntry(10, 20));

		CORNERS_ITEM_GROUP_ENTRIES.forEach((itemGroup, items) -> ItemGroupEvents.modifyEntriesEvent(itemGroup).register(new ModifyEntries() {

			@Override
			public void modifyEntries(FabricItemGroupEntries entries) {
				items.forEach(item -> entries.addItem(item));
			}

		}));
	}

	private static <B extends Block> B get(String id, B block, RegistryKey<ItemGroup> group) {
		Registry.register(Registries.ITEM, TheCorners.id(id), addToItemGroup(new BlockItem(block, new QuiltItemSettings()), group));
		return Registry.register(Registries.BLOCK, TheCorners.id(id), block);
	}

	private static Item addToItemGroup(Item item, RegistryKey<ItemGroup> group) {

		if (!CORNERS_ITEM_GROUP_ENTRIES.containsKey(group)) {
			CORNERS_ITEM_GROUP_ENTRIES.put(group, Sets.newHashSet());
		}

		CORNERS_ITEM_GROUP_ENTRIES.get(group).add(item);

		return item;
	}

}
