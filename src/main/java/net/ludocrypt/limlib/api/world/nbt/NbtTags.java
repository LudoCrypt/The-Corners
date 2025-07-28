package net.ludocrypt.limlib.api.world.nbt;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiPredicate;

import org.apache.commons.lang3.function.TriFunction;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class NbtTags {

	// Map<Group, Map<Structure, Map<Key, Tag>>>
	public final Map<String, Map<Identifier, Map<String, NbtCompound>>> tags;

	// Map<Cache, Map<Group, Set<Structure>>>
	public final Map<String, Map<String, Set<Identifier>>> matchingGroupCache;

	// Map<Cache, Set<Structure>>
	public final Map<String, Set<Identifier>> matchingCache;

	public NbtTags() {
		this.tags = Maps.newHashMap();
		this.matchingGroupCache = Maps.newHashMap();
		this.matchingCache = Maps.newHashMap();
	}

	public static NbtTags parse(NbtGroup group, ResourceManager manager) {
		NbtTags tags = new NbtTags();

		group.forEachGroup((groupId, id) -> {
			NbtCompound readTags = NbtPlacerUtil.loadTags(id, manager);
			Map<String, NbtCompound> tagsMap = tags.tags
				.computeIfAbsent(groupId, (g) -> Maps.newHashMap())
				.computeIfAbsent(id, (g) -> Maps.newHashMap());

			for (String tagKey : readTags.getKeys()) {
				tagsMap.put(tagKey, readTags.getCompound(tagKey));
			}

		});

		return tags;
	}

	public Map<String, Set<Identifier>> matching(TriFunction<String, String, NbtCompound, Boolean> matcher) {

		Map<String, Set<Identifier>> matching = Maps.newHashMap();

		for (Entry<String, Map<Identifier, Map<String, NbtCompound>>> groupEntry : this.tags.entrySet()) {
			Set<Identifier> groupSet = Sets.newHashSet();

			for (Entry<Identifier, Map<String, NbtCompound>> tagsEntry : groupEntry.getValue().entrySet()) {

				for (Entry<String, NbtCompound> tagEntry : tagsEntry.getValue().entrySet()) {

					if (matcher.apply(groupEntry.getKey(), tagEntry.getKey(), tagEntry.getValue())) {
						groupSet.add(tagsEntry.getKey());
					}

				}

			}

			matching.put(groupEntry.getKey(), groupSet);
		}

		return matching;
	}

	public Set<Identifier> matching(BiPredicate<String, NbtCompound> matcher) {

		Set<Identifier> matching = Sets.newHashSet();

		for (Entry<String, Map<Identifier, Map<String, NbtCompound>>> groupEntry : this.tags.entrySet()) {

			for (Entry<Identifier, Map<String, NbtCompound>> tagsEntry : groupEntry.getValue().entrySet()) {

				for (Entry<String, NbtCompound> tagEntry : tagsEntry.getValue().entrySet()) {

					if (matcher.test(tagEntry.getKey(), tagEntry.getValue())) {
						matching.add(tagsEntry.getKey());
					}

				}

			}

		}

		return matching;
	}

	public Map<String, Set<Identifier>> matching(String cache, TriFunction<String, String, NbtCompound, Boolean> matcher) {
		return this.matchingGroupCache.computeIfAbsent(cache, (c) -> matching(matcher));
	}

	public Set<Identifier> matching(String cache, BiPredicate<String, NbtCompound> matcher) {
		this.matchingGroupCache.computeIfAbsent(cache, (c) -> matching((group, tagKey, nbt) -> matcher.test(tagKey, nbt)));
		return this.matchingCache.computeIfAbsent(cache, (c) -> matching(matcher));
	}

	public Set<Identifier> matching(String... cache) {
		Set<Identifier> all = null;

		for (String c : cache) {
			Set<Identifier> cacheSet = this.matchingCache.getOrDefault(c, Sets.newHashSet());

			if (all == null) {
				all = cacheSet;
			} else {
				all.retainAll(cacheSet);
			}

		}

		return all;
	}

	public Map<String, Set<Identifier>> matchingGroups(String... cache) {

		Map<String, Set<Identifier>> matching = Maps.newHashMap();

		for (String key : this.tags.keySet()) {
			Set<Identifier> all = null;

			for (String c : cache) {
				Set<Identifier> cacheSet = this.matchingGroupCache
					.getOrDefault(key, Maps.newHashMap())
					.getOrDefault(c, Sets.newHashSet());

				if (all == null) {
					all = cacheSet;
				} else {
					all.retainAll(cacheSet);
				}

			}

			matching.put(key, all);
		}

		return matching;
	}

	public void closeCache() {
		this.matchingGroupCache.clear();
		this.matchingCache.clear();
	}

}
