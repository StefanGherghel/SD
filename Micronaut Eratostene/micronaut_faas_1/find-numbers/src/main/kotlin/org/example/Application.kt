package org.example

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("org.example")
                .mainClass(Application.javaClass)
                .start()
    }
}