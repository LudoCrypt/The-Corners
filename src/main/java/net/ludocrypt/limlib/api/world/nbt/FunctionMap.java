package net.ludocrypt.limlib.api.world.nbt;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.Maps;

public class FunctionMap<K, V, A> {

	private final Optional<BiFunction<K, A, V>> defaultMapper;
	private Map<K, Function<A, V>> functionMap = Maps.newHashMap();
	private Map<K, V> cache = Maps.newHashMap();

	public FunctionMap(BiFunction<K, A, V> defaultMapper) {
		this.defaultMapper = Optional.of(defaultMapper);
	}

	public FunctionMap() {
		this.defaultMapper = Optional.empty();
	}

	public Function<A, V> put(K key, Function<A, V> compute) {
		return this.functionMap.put(key, compute);
	}

	public Function<A, V> put(K key) {

		if (this.defaultMapper.isEmpty()) {
			throw new UnsupportedOperationException("No default mapper is supplied");
		}

		return this.functionMap.put(key, (arg) -> this.defaultMapper.get().apply(key, arg));
	}

	public V eval(K key, A arg) {

		if (this.functionMap.containsKey(key)) {
			return this.cache.computeIfAbsent(key, (k) -> this.functionMap.get(k).apply(arg));
		} else {
			throw new NullPointerException("Map does not contain key: " + key);
		}

	}

	public Map<K, V> getCache() {
		return this.cache;
	}

	public Map<K, Function<A, V>> getFunctionMap() {
		return this.functionMap;
	}

}
