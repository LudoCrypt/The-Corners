package net.ludocrypt.corners.config;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.ludocrypt.corners.client.render.ChristmasRenderer;

@Config(name = "the_corners")
public class CornerConfig implements ConfigData {

	public boolean delayMusicWithRadio = true;
	public boolean disableStrongShaders = false;
	@ConfigEntry.Gui.CollapsibleObject
	public Christmas christmas = new Christmas();

	public static class Christmas {

		public boolean christmas = false;
		public List<String> leftColors = Lists.newArrayList("#30FF99", "#FE515C", "#FFFFFF");
		public List<String> rightColors = Lists.newArrayList("#FE515C", "#FFFFFF", "#30FF99");

		public boolean isChristmas() {
			return christmas || (LocalDate.now().getMonth() == Month.DECEMBER) || (LocalDate
				.now()
				.getMonth() == Month.JANUARY && LocalDate.now().getDayOfMonth() < 7);
		}

	}

	private static List<String> trimList(List<String> inputList) {
		List<String> trimmedList = new ArrayList<>();
		int limit = Math.min(6, inputList.size());

		for (int i = 0; i < limit; i++) {
			trimmedList.add(inputList.get(i));
		}

		return trimmedList;
	}

	public static CornerConfig get() {
		CornerConfig config = AutoConfig.getConfigHolder(CornerConfig.class).getConfig();

		for (String color : config.christmas.leftColors) {

			try {
				ChristmasRenderer.hexToRGBA(color);
			} catch (IllegalArgumentException e) {
				config.christmas.leftColors.remove(color);
			}

		}

		for (String color : config.christmas.rightColors) {

			try {
				ChristmasRenderer.hexToRGBA(color);
			} catch (IllegalArgumentException e) {
				config.christmas.rightColors.remove(color);
			}

		}

		config.christmas.leftColors = trimList(config.christmas.leftColors);
		config.christmas.rightColors = trimList(config.christmas.rightColors);
		return config;
	}

}
