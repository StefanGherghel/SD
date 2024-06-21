package com.sd.laborator.model

data class Weather(
    val windSpeed: Float = 0.0f,
    val windDegrees: Int = 0,
    val temp: Int = 0,
    val humidity: Int = 0,
    val sunset: Int = 0,
    val minTemp: Int = 0,
    val cloudPct: Int = 0,
    val feelsLike: Int = 0,
    val sunrise: Int = 0,
    val maxTemp: Int = 0
)
