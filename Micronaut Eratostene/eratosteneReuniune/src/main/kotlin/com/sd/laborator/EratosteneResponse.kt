package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class EratosteneResponse {
    private var reunion: Set<Int>? = null
    fun getReunion(): Set<Int>? {
        return reunion
    }

    fun setReunion(reunion: Set<Int>?) {
        this.reunion = reunion
    }
}