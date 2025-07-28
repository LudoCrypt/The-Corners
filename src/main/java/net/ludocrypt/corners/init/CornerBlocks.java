package net.ludocrypt.corners.init;

import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.quiltmc.qsl.block.content.registry.api.FlammableBlockEntry;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.block.CornerBoatItem;
import net.ludocrypt.corners.block.MazeStructureBlock;
import net.ludocrypt.corners.block.NylonFiberBlock;
import net.ludocrypt.corners.block.NylonFiberSlabBlock;
import net.ludocrypt.corners.block.NylonFiberStairsBlock;
import net.ludocrypt.corners.block.OrientableBlock;
import net.ludocrypt.corners.block.RadioBlock;
import net.ludocrypt.corners.block.RailingBlock;
import net.ludocrypt.corners.block.SkyboxGlassBlock;
import net.ludocrypt.corners.block.SkyboxGlassPaneBlock;
import net.ludocrypt.corners.block.SkyboxGlassSlabBlock;
import net.ludocrypt.corners.block.StructureHelperBlock;
import net.ludocrypt.corners.block.ThinPillarBlock;
import net.ludocrypt.corners.block.entity.MazeStructureBlockBlockEntity;
import net.ludocrypt.corners.entity.CornerBoatDispensorBehavior;
import net.ludocrypt.corners.entity.CornerBoatEntity.CornerBoat;
import net.ludocrypt.corners.mixin.SignTypeAccessor;
import net.ludocrypt.corners.world.feature.GaiaSaplingGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.Blocks;
import net.minecraft.block.CeilingHangingSignBlock;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallHangingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HangingSignItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.item.SignItem;
import net.minecraft.item.TallBlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.SignType;
import net.minecraft.util.math.Direction;

public class CornerBlocks {

	public static final Block STONE_PILLAR = get("stone_pillar",
		new ThinPillarBlock(QuiltBlockSettings.copyOf(Blocks.STONE_BRICKS)));

	public static final RadioBlock GROWN_RADIO = get("grown_radio",
		new RadioBlock(null, null, QuiltBlockSettings.copyOf(Blocks.OAK_WOOD)));

	public static final RadioBlock BROKEN_RADIO = get("broken_radio",
		new RadioBlock(null, GROWN_RADIO, QuiltBlockSettings.copyOf(Blocks.OAK_WOOD)));

	public static final RadioBlock WOODEN_RADIO = get("wooden_radio",
		new RadioBlock(Items.GOLD_INGOT, BROKEN_RADIO, QuiltBlockSettings.copyOf(Blocks.OAK_WOOD)));

	public static final RadioBlock TUNED_RADIO = get("tuned_radio",
		new RadioBlock(Items.AMETHYST_SHARD, BROKEN_RADIO, QuiltBlockSettings.copyOf(Blocks.OAK_WOOD)));

	public static final Block DRYWALL = get("drywall", new Block(QuiltBlockSettings.copyOf(Blocks.OAK_PLANKS)));

	public static final Block NYLON_FIBER_BLOCK = get("nylon_fiber_block",
		new NylonFiberBlock(QuiltBlockSettings.copyOf(Blocks.WHITE_WOOL)));

	public static final Block NYLON_FIBER_STAIRS = get("nylon_fiber_stairs",
		new NylonFiberStairsBlock(NYLON_FIBER_BLOCK.getDefaultState(), QuiltBlockSettings.copyOf(Blocks.WHITE_WOOL)));

	public static final Block NYLON_FIBER_SLAB = get("nylon_fiber_slab",
		new NylonFiberSlabBlock(QuiltBlockSettings.copyOf(Blocks.WHITE_WOOL)));

	public static final Block SNOWY_GLASS = get("snowy_glass",
		new SkyboxGlassBlock(QuiltBlockSettings.copyOf(Blocks.GLASS).luminance(3)));

	public static final Block SNOWY_GLASS_PANE = get("snowy_glass_pane",
		new SkyboxGlassPaneBlock(QuiltBlockSettings.copyOf(Blocks.GLASS_PANE).luminance(3)));

	public static final Block SNOWY_GLASS_SLAB = get("snowy_glass_slab",
		new SkyboxGlassSlabBlock(QuiltBlockSettings.copyOf(Blocks.GLASS).luminance(3)));

	public static final Block DARK_RAILING = get("dark_railing", new RailingBlock(QuiltBlockSettings.copyOf(Blocks.STONE)));

	public static final Block DEEP_BOOKSHELF = get("deep_bookshelf",
		new ChiseledBookshelfBlock(QuiltBlockSettings.copyOf(Blocks.OAK_WOOD)));

	public static final Block STRUCTURE_HELPER = get("structure_helper",
		new StructureHelperBlock(QuiltBlockSettings.copyOf(Blocks.JIGSAW)));

	public static final Block MAZE_STRUCTURE_BLOCK = get("maze_structure_block",
		new MazeStructureBlock(QuiltBlockSettings.copyOf(Blocks.STRUCTURE_BLOCK)));

	public static final BlockEntityType<MazeStructureBlockBlockEntity> MAZE_STRUCTURE_BLOCK_ENTITY_TYPE = get(
		"maze_structure_block",
		QuiltBlockEntityTypeBuilder.create(MazeStructureBlockBlockEntity::new, MAZE_STRUCTURE_BLOCK).build());

	public static final BlockSetType GAIA_SET_TYPE;
	public static final SignType GAIA_SIGN_TYPE;
	public static final Block GAIA_PLANKS;
	public static final Block CARVED_GAIA;
	public static final Block GAIA_SAPLING;
	public static final Block GAIA_LOG;
	public static final Block STRIPPED_GAIA_LOG;
	public static final Block GAIA_WOOD;
	public static final Block STRIPPED_GAIA_WOOD;
	public static final Block GAIA_LEAVES;
	public static final Block GAIA_SIGN;
	public static final Block GAIA_WALL_SIGN;
	public static final Block GAIA_HANGING_SIGN;
	public static final Block GAIA_WALL_HANGING_SIGN;
	public static final Block GAIA_PRESSURE_PLATE;
	public static final Block GAIA_TRAPDOOR;
	public static final Block POTTED_GAIA_SAPLING;
	public static final Block GAIA_BUTTON;
	public static final Block GAIA_STAIRS;
	public static final Block GAIA_SLAB;
	public static final Block GAIA_FENCE_GATE;
	public static final Block GAIA_FENCE;
	public static final Block GAIA_DOOR;
	public static final Item GAIA_BOAT;
	public static final Item GAIA_CHEST_BOAT;
	public static final Item GAIA_SIGN_ITEM;
	public static final Item GAIA_HANGING_SIGN_ITEM;
	public static final Item GAIA_DOOR_ITEM;

	public static void init() {
		DispenserBlock.registerBehavior(GAIA_BOAT, new CornerBoatDispensorBehavior(CornerBoat.GAIA, false));
		DispenserBlock.registerBehavior(GAIA_CHEST_BOAT, new CornerBoatDispensorBehavior(CornerBoat.GAIA, true));

		BlockContentRegistries.FLAMMABLE.put(NYLON_FIBER_BLOCK, new FlammableBlockEntry(30, 60));
		BlockContentRegistries.FLAMMABLE.put(NYLON_FIBER_STAIRS, new FlammableBlockEntry(30, 60));
		BlockContentRegistries.FLAMMABLE.put(NYLON_FIBER_SLAB, new FlammableBlockEntry(30, 60));
		BlockContentRegistries.FLAMMABLE.put(DRYWALL, new FlammableBlockEntry(5, 20));
		BlockContentRegistries.FLAMMABLE.put(WOODEN_RADIO, new FlammableBlockEntry(10, 20));
		BlockContentRegistries.FLAMMABLE.put(TUNED_RADIO, new FlammableBlockEntry(10, 20));
		BlockContentRegistries.FLAMMABLE.put(BROKEN_RADIO, new FlammableBlockEntry(10, 20));
		BlockContentRegistries.FLAMMABLE.put(GROWN_RADIO, new FlammableBlockEntry(10, 20));
		BlockContentRegistries.FLAMMABLE.put(STRIPPED_GAIA_LOG, new FlammableBlockEntry(5, 5));
		BlockContentRegistries.FLAMMABLE.put(STRIPPED_GAIA_WOOD, new FlammableBlockEntry(5, 5));
		BlockContentRegistries.FLAMMABLE.put(GAIA_LOG, new FlammableBlockEntry(5, 5));
		BlockContentRegistries.FLAMMABLE.put(GAIA_WOOD, new FlammableBlockEntry(5, 5));
		BlockContentRegistries.FLAMMABLE.put(GAIA_STAIRS, new FlammableBlockEntry(5, 20));
		BlockContentRegistries.FLAMMABLE.put(GAIA_SLAB, new FlammableBlockEntry(5, 20));
		BlockContentRegistries.FLAMMABLE.put(GAIA_PLANKS, new FlammableBlockEntry(5, 20));
		BlockContentRegistries.FLAMMABLE.put(CARVED_GAIA, new FlammableBlockEntry(5, 20));
		BlockContentRegistries.FLAMMABLE.put(GAIA_FENCE, new FlammableBlockEntry(5, 20));
		BlockContentRegistries.FLAMMABLE.put(GAIA_FENCE_GATE, new FlammableBlockEntry(5, 20));
		BlockContentRegistries.FLAMMABLE.put(GAIA_LEAVES, new FlammableBlockEntry(30, 60));

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register((entries) -> {
			entries.addItem(STONE_PILLAR);
			entries.addItem(DARK_RAILING);
			entries.addItem(DRYWALL);
			entries.addItem(NYLON_FIBER_BLOCK);
			entries.addItem(NYLON_FIBER_STAIRS);
			entries.addItem(NYLON_FIBER_SLAB);
			entries.addItem(CARVED_GAIA);
			entries
				.addAfter(Items.CHERRY_BUTTON, GAIA_LOG, GAIA_WOOD, STRIPPED_GAIA_LOG, STRIPPED_GAIA_WOOD, GAIA_PLANKS,
					GAIA_STAIRS, GAIA_SLAB, GAIA_FENCE, GAIA_FENCE_GATE, GAIA_DOOR_ITEM, GAIA_TRAPDOOR, GAIA_PRESSURE_PLATE,
					GAIA_BUTTON);
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COLORED_BLOCKS).register((entries) -> {
			entries.addAfter(Items.PINK_STAINED_GLASS_PANE, SNOWY_GLASS, SNOWY_GLASS_PANE, SNOWY_GLASS_SLAB);
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE_BLOCKS).register((entries) -> {
			entries.addAfter(Items.CHISELED_BOOKSHELF, DEEP_BOOKSHELF);
			entries.addItem(WOODEN_RADIO);
			entries.addItem(TUNED_RADIO);
			entries.addItem(BROKEN_RADIO);
			entries.addItem(GROWN_RADIO);
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL_BLOCKS).register((entries) -> {
			entries.addAfter(Items.CHERRY_LOG, GAIA_LOG);
			entries.addAfter(Items.CHERRY_LEAVES, GAIA_LEAVES);
			entries.addAfter(Items.CHERRY_SAPLING, GAIA_SAPLING);
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL_BLOCKS).register((entries) -> {
			entries.addAfter(Items.CHERRY_HANGING_SIGN, GAIA_SIGN_ITEM, GAIA_HANGING_SIGN_ITEM);
			entries.addAfter(Items.CHISELED_BOOKSHELF, DEEP_BOOKSHELF);
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS_AND_UTILITIES).register((entries) -> {
			entries.addAfter(Items.CHERRY_CHEST_BOAT, GAIA_BOAT, GAIA_CHEST_BOAT);
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR_UTILITIES).register((entries) -> {

			if (entries.getContext().hasPermissions()) {
				entries.addItem(STRUCTURE_HELPER);
				entries.addItem(MAZE_STRUCTURE_BLOCK);
			}

		});
	}

	private static <B extends Block> B getSingle(String id, B block) {
		return Registry.register(Registries.BLOCK, TheCorners.id(id), block);
	}

	private static <B extends Block> B get(String id, B block) {
		Registry.register(Registries.ITEM, TheCorners.id(id), new BlockItem(block, new QuiltItemSettings()));
		return Registry.register(Registries.BLOCK, TheCorners.id(id), block);
	}

	private static <I extends Item> I get(String id, I item) {
		return Registry.register(Registries.ITEM, TheCorners.id(id), item);
	}

	private static <T extends BlockEntity, B extends BlockEntityType<T>> B get(String id, B blockEntity) {
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, TheCorners.id(id), blockEntity);
	}

	static {
		GAIA_SET_TYPE = BlockSetTypeBuilder.copyOf(BlockSetType.SPRUCE).build(TheCorners.id("gaia"));

		GAIA_SIGN_TYPE = SignTypeAccessor.callRegister(new SignType("corners:gaia", BlockSetType.SPRUCE));

		GAIA_PLANKS = get("gaia_planks", new Block(QuiltBlockSettings.copyOf(Blocks.SPRUCE_PLANKS)));

		CARVED_GAIA = get("carved_gaia", new OrientableBlock(QuiltBlockSettings.copyOf(Blocks.SPRUCE_PLANKS)));

		GAIA_SAPLING = get("gaia_sapling", new SaplingBlock(new GaiaSaplingGenerator(),
			QuiltBlockSettings.copyOf(Blocks.SPRUCE_SAPLING).mapColor(MapColor.GOLD)));

		GAIA_LOG = get("gaia_log",
			new PillarBlock(QuiltBlockSettings
				.copyOf(Blocks.SPRUCE_LOG)
				.mapColor(state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? MapColor.PODZOL : MapColor.GOLD)));

		STRIPPED_GAIA_LOG = get("stripped_gaia_log",
			new PillarBlock(QuiltBlockSettings.copyOf(Blocks.STRIPPED_SPRUCE_LOG).mapColor(MapColor.PODZOL)));

		GAIA_WOOD = get("gaia_wood", new PillarBlock(QuiltBlockSettings.copyOf(Blocks.SPRUCE_WOOD).mapColor(MapColor.GOLD)));

		STRIPPED_GAIA_WOOD = get("stripped_gaia_wood",
			new PillarBlock(QuiltBlockSettings.copyOf(Blocks.STRIPPED_SPRUCE_WOOD)));

		GAIA_LEAVES = get("gaia_leaves", new LeavesBlock(QuiltBlockSettings.copyOf(Blocks.SPRUCE_LEAVES)));

		GAIA_SIGN = getSingle("gaia_sign", new SignBlock(QuiltBlockSettings.copyOf(Blocks.SPRUCE_SIGN), GAIA_SIGN_TYPE));

		GAIA_WALL_SIGN = getSingle("gaia_wall_sign",
			new WallSignBlock(QuiltBlockSettings.copyOf(Blocks.SPRUCE_WALL_SIGN).dropsLike(GAIA_SIGN), GAIA_SIGN_TYPE));

		GAIA_HANGING_SIGN = getSingle("gaia_hanging_sign",
			new CeilingHangingSignBlock(QuiltBlockSettings.copyOf(Blocks.SPRUCE_HANGING_SIGN), GAIA_SIGN_TYPE));

		GAIA_WALL_HANGING_SIGN = getSingle("gaia_wall_hanging_sign", new WallHangingSignBlock(
			QuiltBlockSettings.copyOf(Blocks.SPRUCE_WALL_HANGING_SIGN).dropsLike(GAIA_HANGING_SIGN), GAIA_SIGN_TYPE));

		GAIA_PRESSURE_PLATE = get("gaia_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING,
			QuiltBlockSettings.copyOf(Blocks.SPRUCE_PRESSURE_PLATE), GAIA_SET_TYPE));

		GAIA_TRAPDOOR = get("gaia_trapdoor",
			new TrapdoorBlock(QuiltBlockSettings.copyOf(Blocks.SPRUCE_TRAPDOOR), GAIA_SET_TYPE));

		POTTED_GAIA_SAPLING = getSingle("potted_gaia_sapling", Blocks.createFlowerPotBlock(GAIA_SAPLING));
		GAIA_BUTTON = get("gaia_button", Blocks.createButtonBlock(GAIA_SET_TYPE));

		GAIA_STAIRS = get("gaia_stairs",
			new StairsBlock(GAIA_PLANKS.getDefaultState(), QuiltBlockSettings.copyOf(GAIA_PLANKS)));
		GAIA_SLAB = get("gaia_slab", new SlabBlock(QuiltBlockSettings.copyOf(Blocks.SPRUCE_SLAB)));

		GAIA_FENCE_GATE = get("gaia_fence_gate",
			new FenceGateBlock(QuiltBlockSettings.copyOf(Blocks.SPRUCE_FENCE_GATE), GAIA_SIGN_TYPE));
		GAIA_FENCE = get("gaia_fence", new FenceBlock(QuiltBlockSettings.copyOf(Blocks.SPRUCE_FENCE)));

		GAIA_DOOR = getSingle("gaia_door", new DoorBlock(QuiltBlockSettings.copyOf(Blocks.SPRUCE_DOOR), GAIA_SET_TYPE));

		GAIA_BOAT = get("gaia_boat", new CornerBoatItem(false, CornerBoat.GAIA, new Item.Settings().maxCount(1)));

		GAIA_CHEST_BOAT = get("gaia_chest_boat", new CornerBoatItem(true, CornerBoat.GAIA, new Item.Settings().maxCount(1)));

		GAIA_SIGN_ITEM = get("gaia_sign", new SignItem(new Item.Settings().maxCount(16), GAIA_SIGN, GAIA_WALL_SIGN));

		GAIA_HANGING_SIGN_ITEM = get("gaia_hanging_sign",
			new HangingSignItem(GAIA_HANGING_SIGN, GAIA_WALL_HANGING_SIGN, new Item.Settings().maxCount(16)));

		GAIA_DOOR_ITEM = get("gaia_door", new TallBlockItem(GAIA_DOOR, new Item.Settings()));
	}

}
