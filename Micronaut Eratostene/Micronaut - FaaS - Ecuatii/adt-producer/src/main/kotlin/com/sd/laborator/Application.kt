package com.sd.laborator

import com.sd.laborator.function.ProducerFunction
import com.sd.laborator.model.AdtData
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.runtime.Micronaut

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(Application::class.java, *args)
    }

    @Controller("/generate-adt")
    class LambdaController {
        @Post("/")
        fun execute(@Body adtData: AdtData) {
            handler.accept(adtData)
        }

        companion object {
            private val handler = ProducerFunction()
        }
    }
}