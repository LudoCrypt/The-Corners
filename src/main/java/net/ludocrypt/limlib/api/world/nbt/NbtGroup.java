package net.ludocrypt.limlib.api.world.nbt;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;
import net.minecraft.util.random.RandomGenerator;

public class NbtGroup {

	public static final Codec<NbtGroup> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Identifier.CODEC.fieldOf("id").stable().forGetter((group) -> {
			return group.id;
		}), Codec.unboundedMap(Codec.STRING, Codec.list(Codec.STRING)).fieldOf("groups").stable().forGetter((group) -> {
			return group.groups;
		})).apply(instance, instance.stable(NbtGroup::new));
	});

	Identifier id;
	Map<String, List<String>> groups;

	public NbtGroup(Identifier id, Map<String, List<String>> groups) {
		this.id = id;
		this.groups = groups;
	}

	public Identifier nbtId(String group, String nbt) {
		return new Identifier(this.id.getNamespace(),
			"structures/nbt/" + this.id.getPath() + "/" + group + "/" + nbt + ".nbt");
	}

	public Identifier pick(RandomGenerator random, String key) {

		if (!groups.containsKey(key)) {
			throw new NullPointerException();
		}

		List<String> group = groups.get(key);
		return nbtId(key, group.get(random.nextInt(group.size())));
	}

	public String chooseGroup(RandomGenerator random, String... keys) {
		int[] sizes = new int[keys.length];

		for (int i = 0; i < keys.length; i++) {
			int extra = 0;

			if (i > 0) {
				extra = sizes[i - 1];
			}

			sizes[i] = extra + groups.get(keys[i]).size();
		}

		int g = random.nextInt(sizes[keys.length - 1]);

		for (int i = 0; i < keys.length; i++) {

			if (g < sizes[i]) {
				return keys[i];
			}

		}

		throw new UnsupportedOperationException("Failed to retrieve key");
	}

	public boolean contains(String key, String nbt) {

		if (!groups.containsKey(key)) {
			return false;
		}

		List<String> group = groups.get(key);
		return group.contains(nbt);
	}

	public boolean contains(String key) {
		return groups.containsKey(key);
	}

	public void forEach(Consumer<Identifier> runnable) {
		forEachGroup((group, id) -> runnable.accept(id));
	}

	public void forEachGroup(BiConsumer<String, Identifier> runnable) {

		for (Entry<String, List<String>> entry : groups.entrySet()) {

			for (String nbt : entry.getValue()) {
				runnable.accept(entry.getKey(), nbtId(entry.getKey(), nbt));
			}

		}

	}

	public <A, V> void fill(FunctionMap<Identifier, A, V> map) {
		forEach(map::put);
	}

	public Identifier getId() {
		return id;
	}

	public Map<String, List<String>> getGroups() {
		return groups;
	}

	public static class Builder {

		Identifier id;
		Map<String, List<String>> groups = Maps.newHashMap();

		public static Builder create(Identifier id) {
			Builder builder = new Builder();
			builder.id = id;
			return builder;
		}

		public Builder with(String group, String base, int from, int to) {
			List<String> list = Lists.newArrayList();

			for (int i = from; i <= to; i++) {
				list.add(base + "_" + i);
			}

			groups.put(group, list);
			return this;
		}

		public Builder with(String group, String... base) {
			groups.put(group, List.of(base));
			return this;
		}

		public Builder with(String base, int from, int to) {
			return with(base, base, from, to);
		}

		public Builder with(String base) {
			return with(base, base);
		}

		public NbtGroup build() {
			return new NbtGroup(id, groups);
		}

	}

}
