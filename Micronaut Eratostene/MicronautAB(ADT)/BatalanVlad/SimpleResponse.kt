package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class SimpleResponse {
	private var pairs:List<Pair<Int, Int>> = listOf()

	fun getPairs():List<Pair<Int, Int>>{
		return pairs
	}

	fun setPairs(pairs: List<Pair<Int, Int>>) {
		this.pairs = pairs
	}
}


