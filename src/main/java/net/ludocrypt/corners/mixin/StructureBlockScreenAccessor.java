package net.ludocrypt.corners.mixin;

import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructureBlockScreen.class)
public interface StructureBlockScreenAccessor {

	@Invoker
	void callUpdateWidgets(StructureBlockMode mode);

	@Accessor
	TextFieldWidget getInputMetadata();

}
