package com.sd.laborator.interfaces

import com.sd.laborator.pojo.ForecastData

interface DataLoggerInterface {
    fun logData(weatherForecastJsonResponse : ForecastData)
    fun logData(message : String)
}