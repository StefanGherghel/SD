package com.sd.laborator

import io.micronaut.runtime.Micronaut

// in asta parca nu trebuia schimbat nimic
object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("com.sd.laborator")
                .mainClass(Application.javaClass)
                .start()
    }
}