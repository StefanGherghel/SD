package com.sd.laborator.pojo

data class FilteredWeatherForecastData(
    var location: String,
    var minTemp: Int, // grade celsius
    var maxTemp: Int
) : ForecastData
{
    override fun toString(): String {
        return "location='$location', minTemp=$minTemp, maxTemp=$maxTemp"
    }
}