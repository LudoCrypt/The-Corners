package net.ludocrypt.corners.util;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Maps;

public class CachedFunction<T, R> implements Function<T, R> {

	public final Function<T, R> wrapper;
	public final Map<T, R> cache = Maps.newHashMap();

	public CachedFunction(Function<T, R> function) {
		this.wrapper = function;
	}

	public R apply(T object) {
		return this.cache.computeIfAbsent(object, wrapper);
	}

	public String toString() {
		return "memoize/1[function=" + wrapper + ", size=" + this.cache.size() + "]";
	}

	public static <T, R> CachedFunction<T, R> memoize(Function<T, R> function) {
		return new CachedFunction<T, R>(function);
	}

}
