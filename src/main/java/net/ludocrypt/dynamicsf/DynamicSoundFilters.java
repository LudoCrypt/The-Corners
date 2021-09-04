/*
 * Modified from https://gitlab.com/mikenrafter1/mc-dyn-sfx/-/releases/1.4.1
 * License included inside Jar.
 */
/*
 * Copyright (c) 2021 Andr? Schweiger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ludocrypt.dynamicsf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.ludocrypt.dynamicsf.filter.FilterManager;

@Environment(EnvType.CLIENT)
public class DynamicSoundFilters implements ClientModInitializer {
	private static final FilterManager FILTER_MANAGER = new FilterManager();
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(FILTER_MANAGER::updateGlobal);
	}

	public static FilterManager getFilterManager() {
		return FILTER_MANAGER;
	}

	public static Logger getLogger() {
		return LOGGER;
	}

}
