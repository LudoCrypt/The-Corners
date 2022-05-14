package net.ludocrypt.corners.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class RailingBlock extends FenceBlock {

	public static final IntProperty LAYERS = IntProperty.of("layers", 0, 8);
	protected static final VoxelShape[] LAYERS_TO_OUTLINE = new VoxelShape[] { VoxelShapes.empty(), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0) };
	protected static final VoxelShape[] LAYERS_TO_COLLISION = new VoxelShape[] { VoxelShapes.empty(), VoxelShapes.empty(), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0) };
	protected final VoxelShape[] boundingShapes;

	public RailingBlock(Settings settings) {
		super(settings);
		this.boundingShapes = this.createShapes(2.0F, 2.0F, 12.0F, 0.0f, 12.0F);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(LAYERS);
	}

	@Override
	protected boolean canConnectToFence(BlockState state) {
		return state.getBlock() instanceof RailingBlock;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.union(this.boundingShapes[this.getShapeIndex(state)], Block.createCuboidShape(6.0D, 12.0D, 6.0D, 10.0D, 16.0D, 10.0D), LAYERS_TO_OUTLINE[state.get(LAYERS)]);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.union(this.boundingShapes[this.getShapeIndex(state)], Block.createCuboidShape(6.0D, 12.0D, 6.0D, 10.0D, 16.0D, 10.0D), LAYERS_TO_COLLISION[state.get(LAYERS)]);
	}

	@Override
	@SuppressWarnings("deprecation")
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.getItem() == Items.SNOW) {
			if (state.get(LAYERS) < 8 && Blocks.SNOW.canPlaceAt(Blocks.SNOW.getDefaultState(), world, pos)) {
				world.setBlockState(pos, state.cycle(LAYERS));
				world.playSound(null, pos, SoundEvents.BLOCK_SNOW_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
				return ActionResult.SUCCESS;
			}
		}
		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
		super.onBroken(world, pos, state);
		if (state.get(LAYERS) > 0) {
			world.setBlockState(pos, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, state.get(LAYERS)), Block.NOTIFY_ALL);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState placementState = super.getPlacementState(ctx);
		BlockState state = ctx.getWorld().getBlockState(ctx.getBlockPos());

		if (state.isOf(Blocks.SNOW)) {
			placementState = placementState.with(LAYERS, state.get(SnowBlock.LAYERS));
		}

		return placementState;
	}

}
