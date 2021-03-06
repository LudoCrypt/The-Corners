package net.ludocrypt.corners.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "the_corners")
public class CornerConfig implements ConfigData {

	@ConfigEntry.Gui.Tooltip()
	@ConfigEntry.Gui.RequiresRestart
	public boolean condensedDimensions = false;

	@ConfigEntry.Gui.Tooltip()
	public boolean delayMusicWithRadio = true;

	@ConfigEntry.Gui.Tooltip()
	public boolean disableStrongShaders = false;

	public static CornerConfig getInstance() {
		return AutoConfig.getConfigHolder(CornerConfig.class).getConfig();
	}

	static {
		AutoConfig.register(CornerConfig.class, GsonConfigSerializer::new);
	}

}
