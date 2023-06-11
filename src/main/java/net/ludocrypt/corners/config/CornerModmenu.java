package net.ludocrypt.corners.config;

import org.quiltmc.loader.api.minecraft.ClientOnly;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.autoconfig.AutoConfig;

@ClientOnly
public class CornerModmenu implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> AutoConfig.getConfigScreen(CornerConfig.class, parent).get();
	}

}
