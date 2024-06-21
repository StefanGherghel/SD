package com.sd.laborator.controllers

import com.sd.laborator.interfaces.CityListInterface
import com.sd.laborator.interfaces.LocationSearchInterface
import com.sd.laborator.interfaces.OrchestratorServiceInterface
import com.sd.laborator.interfaces.WeatherForecastInterface
import com.sd.laborator.pojo.WeatherForecastData
import com.sd.laborator.services.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class WeatherAppController {
    @Autowired
    private lateinit var orchestratorService: OrchestratorServiceInterface

    @RequestMapping("/getforecast/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getForecast(@PathVariable location: String): String {
        // se incearca preluarea WOEID-ului locaţiei primite in URL
        // pe baza ID-ului de locaţie, se interoghează al doilea serviciu care returnează datele meteo
        // încapsulate într-un obiect POJO
        val rawForecastData: WeatherForecastData = orchestratorService.getForecastData(location)

        // fiind obiect POJO, funcţia toString() este suprascrisă pentru o afişare mai prietenoasă
        return rawForecastData.toString()
    }

    // path for testing the City List Service
    @RequestMapping("/regressionangle", method = [RequestMethod.GET])
    @ResponseBody
    fun getRegressionAngle(): Double {
        val angle = orchestratorService.getRegressionAngle()

        // fiind obiect POJO, funcţia toString() este suprascrisă pentru o afişare mai prietenoasă
        return angle
    }

}