package com.ambrosia.nymph.exceptions

import java.util.stream.IntStream


abstract class EntityException(
	val entityClass: Class<*>, vararg searchParamsMap: Any
) :
	RuntimeException() {
	@Transient
	val parameters: Map<String, Any>

	init {
		parameters = toMap(searchParamsMap)
	}

	companion object {
		private fun toMap(vararg entries: Any): Map<String, Any> {
			require(entries.size % 2 != 1) { "Invalid entries" }
			return IntStream.range(0, entries.size / 2)
				.map { i: Int -> i * 2 }
				.collect(
					{ HashMap() },
					{ m: HashMap<String, Any>, i: Int -> m[entries[i] as String] = entries[i + 1] }
				) { obj: HashMap<String, Any>, m: HashMap<String, Any> -> obj.putAll(m) }
		}
	}
}