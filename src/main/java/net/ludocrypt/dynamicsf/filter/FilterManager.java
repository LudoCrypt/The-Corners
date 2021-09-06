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
package net.ludocrypt.dynamicsf.filter;

import org.lwjgl.openal.AL11;
import org.lwjgl.openal.EXTEfx;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.dynamicsf.config.ReverbSettings;
import net.ludocrypt.dynamicsf.init.WorldReverbRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class FilterManager {
	private ReverbSettings data = WorldReverbRegistry.DEFAULT;
	private Vec3d clientPos = new Vec3d(0, 0, 0);
	private boolean verdict = false;
	private boolean update = false;

	public void updateGlobal(final MinecraftClient client) {
		update = !update;
		verdict = !(client.world == null || client.player == null) && client.isRunning();

		if (update) {
			if (verdict) {
				clientPos = client.player.getPos().add(0, client.player.getEyeHeight(client.player.getPose()), 0);
				data = WorldReverbRegistry.getCurrent(client);
			}
		}

		ReverbFilter.updateGlobal(verdict, client, data, clientPos);
	}

	public void updateSoundInstance(final SoundInstance soundInstance, final int sourceID) {
		if (ReverbSettings.isIgnoredSoundEvent(soundInstance.getId())) {
			return;
		}
		final boolean reverberate = ReverbFilter.updateSoundInstance(soundInstance);
		final int reverbSlot = reverberate ? ReverbFilter.getSlot() : 0;

		for (int i = 0; i < 2; i++) {
			AL11.alSourcei(sourceID, EXTEfx.AL_DIRECT_FILTER, 0);
			AL11.alSource3i(sourceID, EXTEfx.AL_AUXILIARY_SEND_FILTER, reverbSlot, 0, 0);
			final int error = AL11.alGetError();
			if (error == AL11.AL_NO_ERROR) {
				break;
			} else {
				ReverbFilter.reinit();
				System.err.println("OpenAl error " + error);
			}
		}
	}
}
