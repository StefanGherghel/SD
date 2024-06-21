package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class EratosteneResponse {
	private var message: String? = null
	private var intersection: Set<Int>? = null

	fun getIntersection(): Set<Int>? {
		return intersection
	}

	fun setIntersection(primes: Set<Int>) {
		this.intersection = primes
	}

	fun getMessage(): String? {
		return message
	}

	fun setMessage(message: String?) {
		this.message = message
	}
}


