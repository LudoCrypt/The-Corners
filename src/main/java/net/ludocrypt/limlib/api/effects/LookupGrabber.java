package net.ludocrypt.limlib.api.effects;

import java.util.Optional;

import net.minecraft.registry.Holder;
import net.minecraft.registry.HolderLookup;
import net.minecraft.registry.RegistryKey;

public class LookupGrabber {

	public static <T> Optional<T> snatch(HolderLookup<T> lookup, RegistryKey<T> key) {
		Optional<Holder.Reference<T>> holderOptional = lookup.getHolder(key);

		if (holderOptional.isPresent()) {
			Holder.Reference<T> holder = holderOptional.get();
			try {
				T held = holder.value();
				return Optional.of(held);
			} catch (IllegalStateException e) {
				return Optional.empty();
			}
		}

		return Optional.empty();
	}

}
