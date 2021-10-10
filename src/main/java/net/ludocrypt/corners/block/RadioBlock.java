package net.ludocrypt.corners.block;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.packet.ClientToServerPackets;
import net.ludocrypt.corners.util.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RadioBlock extends HorizontalFacingBlock {

	public static final BooleanProperty POWERED = Properties.POWERED;

	public RadioBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(POWERED, false));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(POWERED, FACING);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isReceivingRedstonePower(pos)) {
			sendOut(world, pos, !state.get(POWERED));
			if (state.get(POWERED)) {
				PlayerUtil.grantAdvancement(player, TheCorners.id("nevermind_radio"));
			}
			world.setBlockState(pos, state.cycle(POWERED));
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (world.isReceivingRedstonePower(pos)) {
			if (!state.get(POWERED)) {
				sendOut(world, pos, true);
			}
			world.setBlockState(pos, state.with(POWERED, true));
		} else {
			if (state.get(POWERED)) {
				sendOut(world, pos, false);
			}
			world.setBlockState(pos, state.with(POWERED, false));
		}
		super.neighborUpdate(state, world, pos, block, fromPos, notify);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		boolean power = ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos());
		if (power) {
			sendOut(ctx.getWorld(), ctx.getBlockPos(), true);
		}
		return super.getPlacementState(ctx).with(POWERED, power).with(FACING, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return state.get(POWERED);
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return hasComparatorOutput(state) ? 1 : 0;
	}

	public static void sendOut(World world, BlockPos pos, boolean powered) {
		if (!world.isClient) {
			PacketByteBuf buf = PacketByteBufs.create();
			buf.writeBlockPos(pos);
			buf.writeBoolean(powered);

			for (ServerPlayerEntity serverPlayer : PlayerLookup.tracking((ServerWorld) world, pos)) {
				ServerPlayNetworking.send(serverPlayer, ClientToServerPackets.PLAY_RADIO, buf);
			}
		}
	}

}
