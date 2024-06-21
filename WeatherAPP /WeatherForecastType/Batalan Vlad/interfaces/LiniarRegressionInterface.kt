package com.sd.laborator.interfaces

import com.sd.laborator.pojo.WeatherForecastData
import java.io.File

interface LiniarRegressionInterface {
    fun getRegressionAngle(forecastList : List<WeatherForecastData>): Double
}