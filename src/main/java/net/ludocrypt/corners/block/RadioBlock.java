package net.ludocrypt.corners.block;

import java.util.Map;

import javax.annotation.Nullable;

import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import com.google.common.collect.Maps;

import net.ludocrypt.corners.packet.ClientToServerPackets;
import net.ludocrypt.corners.world.feature.GaiaSaplingGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class RadioBlock extends HorizontalFacingBlock {

	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final Map<Item, RadioBlock> CORES = Maps.newHashMap();
	public final @Nullable Item core;
	public final @Nullable RadioBlock empty;

	public RadioBlock(@Nullable Item core, @Nullable RadioBlock empty, Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(POWERED, false));
		this.core = core;
		this.empty = empty;

		if (core != null && empty != null) {
			CORES.put(core, this);
		}

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
			world.setBlockState(pos, state.cycle(POWERED));
			return ActionResult.SUCCESS;
		} else if (player.getStackInHand(hand).isOf(Items.BONE_MEAL)) {

			if (this.core == null) {

				if (!world.isClient()) {

					if (!player.getAbilities().creativeMode) {
						player.getStackInHand(hand).decrement(1);
					}

					if (this.empty != null) {
						world.setBlockState(pos, of(state, empty));
					} else {

						if (world instanceof ServerWorld s) {
							new GaiaSaplingGenerator().generateRadio(s, s.getChunkManager().getChunkGenerator(), pos, state, s.getRandom());
						}

					}

					world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, pos, 0);
				}

				return ActionResult.SUCCESS;
			}

		} else if (hit.getSide().equals(state.get(FACING))) {

			if (core != null && empty != null) {

				if (!world.isClient()) {
					player.getInventory().offerOrDrop(core.getDefaultStack());
					world.setBlockState(pos, of(state, empty));
				}

				world.playSound(pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				return ActionResult.SUCCESS;
			} else if (CORES.containsKey(player.getStackInHand(hand).getItem())) {

				if (!world.isClient()) {
					world.setBlockState(pos, of(state, CORES.get(player.getStackInHand(hand).getItem())));

					if (!player.getAbilities().creativeMode) {
						player.getStackInHand(hand).decrement(1);
					}

				}

				world.playSound(pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, 0.5F, false);
				return ActionResult.SUCCESS;
			}

		}

		return ActionResult.PASS;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static BlockState of(BlockState from, Block to) {
		BlockState newState = to.getDefaultState();

		for (Property p : from.getProperties()) {

			if (newState.getProperties().contains(p)) {
				newState = newState.with(p, from.get(p));
			}

		}

		return newState;
	}

	@SuppressWarnings("deprecation")
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
