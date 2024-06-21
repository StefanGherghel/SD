package com.sd.laborator

import com.google.gson.Gson
import com.sd.laborator.model.Weather
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

@Service
class GetWeatherData {
    companion object CONSTANTS {
        private const val address = "https://api.api-ninjas.com/v1/weather?"
        private const val token = "XNFHIWuB/sKkgaIA17gIXA==lkyJw4irMrOgGUgE"
    }

    fun generateData(cities: List<String>): Map<String, Weather> = runBlocking {
        println("Sa trecem la treaba")
        val results = cities.map {
                try {
                    val urlConnection = URL("${address}city=${it}").openConnection() as HttpURLConnection
                    urlConnection.setRequestProperty("X-Api-Key", token)
                    urlConnection.setRequestProperty("accept", "application/json")
                    val response = urlConnection.inputStream
                    val rawResponse = BufferedReader(response.reader()).readText()
                    Gson().fromJson(rawResponse, Weather::class.java)
                }
                catch (e: Exception) {
                    println("O mica problema")
                    Weather()
                }
        }
        return@runBlocking cities.zip(results).toMap()
    }
}