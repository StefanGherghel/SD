package com.sd.laborator

import org.springframework.stereotype.Service
import java.io.File

@Service
class CitiesGenerator {
    companion object CONSTANTS {
        private const val PATH = "src/main/resources/cities.txt"
    }

    fun readCities(): List<String> {
        return File(PATH).readLines(Charsets.UTF_8)
    }
}