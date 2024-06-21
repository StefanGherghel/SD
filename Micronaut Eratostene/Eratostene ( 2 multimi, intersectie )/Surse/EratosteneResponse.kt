package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class EratosteneResponse {
	private var intersection: Set<Int>? = null
	fun getIntersection(): Set<Int>? {
		return intersection
	}

	fun setIntersection(intersection: Set<Int>?) {
		this.intersection = intersection
	}
}


