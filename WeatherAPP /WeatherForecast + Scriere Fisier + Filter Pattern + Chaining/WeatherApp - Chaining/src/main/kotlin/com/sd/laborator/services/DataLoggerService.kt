package com.sd.laborator.services

import com.sd.laborator.interfaces.DataLoggerInterface
import com.sd.laborator.pojo.ForecastData
import com.sd.laborator.pojo.WeatherForecastData
import org.springframework.stereotype.Service
import java.io.File

@Service
class DataLoggerService : DataLoggerInterface{
    override fun logData(weatherForecastJsonResponse :  ForecastData) {
        File("log.txt").appendText(weatherForecastJsonResponse.toString())
    }

    override fun logData(message : String){
        File("log.txt").appendText(message)
    }
}