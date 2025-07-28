package net.ludocrypt.corners.client.screen;

import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.block.entity.MazeStructureBlockBlockEntity;
import net.ludocrypt.corners.mixin.StructureBlockScreenAccessor;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

public class MazeStructureBlockScreen extends StructureBlockScreen {

	private List<CyclingButtonWidget<Byte>> connectionsUp = Lists.newArrayList();
	private List<CyclingButtonWidget<Byte>> connectionsDown = Lists.newArrayList();
	private List<CyclingButtonWidget<Byte>> connectionsLeft = Lists.newArrayList();
	private List<CyclingButtonWidget<Byte>> connectionsRight = Lists.newArrayList();

	private ButtonWidget goUp;
	private ButtonWidget goRight;
	private ButtonWidget goBoth;

	private ButtonWidget save;

	private final MazeStructureBlockBlockEntity mazeStructureBlock;

	public MazeStructureBlockScreen(MazeStructureBlockBlockEntity structureBlock) {
		super(structureBlock);
		this.mazeStructureBlock = structureBlock;
	}

	@Override
	protected void init() {
		this.connectionsUp = createConnections(mazeStructureBlock.getConnectionsUp());
		this.connectionsDown = createConnections(mazeStructureBlock.getConnectionsDown());
		this.connectionsLeft = createConnections(mazeStructureBlock.getConnectionsLeft());
		this.connectionsRight = createConnections(mazeStructureBlock.getConnectionsRight());

		this.goUp = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
			pressList(connectionsLeft, this.mazeStructureBlock.getConnectionsLeft());
			pressList(connectionsRight, this.mazeStructureBlock.getConnectionsRight());
		}).positionAndSize(this.width / 2, this.height / 2 - 40, 20, 20).build());
		this.goRight = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
			pressList(connectionsUp, this.mazeStructureBlock.getConnectionsUp());
			pressList(connectionsDown, this.mazeStructureBlock.getConnectionsDown());
		}).positionAndSize(this.width / 2 + 20, this.height / 2 - 20, 20, 20).build());
		this.goBoth = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
			pressList(connectionsUp, this.mazeStructureBlock.getConnectionsUp());
			pressList(connectionsDown, this.mazeStructureBlock.getConnectionsDown());
			pressList(connectionsLeft, this.mazeStructureBlock.getConnectionsLeft());
			pressList(connectionsRight, this.mazeStructureBlock.getConnectionsRight());
		}).positionAndSize(this.width / 2 + 20, this.height / 2 - 40, 20, 20).build());

		this.save = this
			.addDrawableChild(ButtonWidget
				.builder(Text.translatable("structure_block.button.save"), button -> this.send())
				.positionAndSize(this.width / 2 + 4 + 100, 185, 50, 20)
				.build());

		super.init();
	}

	private List<CyclingButtonWidget<Byte>> createConnections(List<Byte> connections) {
		List<CyclingButtonWidget<Byte>> widgets = Lists.newArrayList();

		for (int i = 0; i < connections.size(); i++) {
			widgets.add(i, createWidget(connections, i));
		}

		return widgets;
	}

	private CyclingButtonWidget<Byte> createWidget(List<Byte> connections, int i) {
		return this
			.addDrawableChild(CyclingButtonWidget
				.builder((Byte b) -> Text.of(Byte.toString(b.byteValue())))
				.values(ImmutableList.of((byte) 0, (byte) 1), ImmutableList.of((byte) 2))
				.initially(connections.get(i))
				.omitKeyText()
				.build(0, 0, 20, 20, Text.empty(), (button, val) -> connections.set(i, val)));
	}

	private void pressList(List<CyclingButtonWidget<Byte>> widgetConnections, List<Byte> mazeConnections) {

		if (GLFW
			.glfwGetKey(this.client.getWindow().getHandle(),
				this.client.options.sneakKey.getDefaultKey().getKeyCode()) == GLFW.GLFW_PRESS) {

			if (!mazeConnections.isEmpty()) {
				mazeConnections.remove(mazeConnections.size() - 1);
			}

		} else {
			mazeConnections.add((byte) 0);
		}

		widgetConnections.forEach(this::remove);
		widgetConnections.clear();
		widgetConnections.addAll(createConnections(mazeConnections));
		this.updateWidgets(StructureBlockMode.DATA);

	}

	@Override
	public void updateWidgets(StructureBlockMode mode) {
		super.updateWidgets(mode);

		if (mode == StructureBlockMode.DATA) {
			((StructureBlockScreenAccessor) this).getInputMetadata().setVisible(false);

			for (int i = 0; i < connectionsUp.size(); i++) {
				CyclingButtonWidget<Byte> v = connectionsUp.get(i);
				v.visible = true;
				v.setX(this.width / 2 + 20 + i * 20);
				v.setY(this.height / 2 - 40 - 20 * connectionsLeft.size());
			}

			for (int i = 0; i < connectionsDown.size(); i++) {
				CyclingButtonWidget<Byte> v = connectionsDown.get(i);
				v.visible = true;
				v.setX(this.width / 2 + 20 + i * 20);
				v.setY(this.height / 2 - 20);
			}

			for (int i = 0; i < connectionsRight.size(); i++) {
				CyclingButtonWidget<Byte> v = connectionsRight.get(i);
				v.visible = true;
				v.setX(this.width / 2 + 20 + connectionsUp.size() * 20);
				v.setY(this.height / 2 - 40 - 20 * i);
			}

			for (int i = 0; i < connectionsLeft.size(); i++) {
				CyclingButtonWidget<Byte> v = connectionsLeft.get(i);
				v.visible = true;
				v.setX(this.width / 2);
				v.setY(this.height / 2 - 40 - 20 * i);
			}

			goUp.visible = true;
			goRight.visible = true;
			goBoth.visible = true;
			save.visible = true;

			goUp.setY(this.height / 2 - 40 - 20 * connectionsLeft.size());
			goRight.setX(this.width / 2 + 20 + 20 * connectionsUp.size());

			goBoth.setY(this.height / 2 - 40 - 20 * connectionsLeft.size());
			goBoth.setX(this.width / 2 + 20 + 20 * connectionsUp.size());

		} else {

			for (CyclingButtonWidget<Byte> v : connectionsUp) {
				v.visible = false;
			}

			for (CyclingButtonWidget<Byte> v : connectionsDown) {
				v.visible = false;
			}

			for (CyclingButtonWidget<Byte> v : connectionsLeft) {
				v.visible = false;
			}

			for (CyclingButtonWidget<Byte> v : connectionsRight) {
				v.visible = false;
			}

			goUp.visible = false;
			goRight.visible = false;
			goBoth.visible = false;
			save.visible = false;

		}

	}

	private void send() {
		PacketByteBuf buf = PacketByteBufs.create();

		buf.writeBlockPos(this.mazeStructureBlock.getPos());
		buf.writeNbt(this.mazeStructureBlock.writeListToNbt());

		ClientPlayNetworking.send(TheCorners.id("structure_block"), buf);
	}

}
