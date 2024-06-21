package com.sd.laborator.microservices

import com.sd.laborator.interfaces.BeerDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class BeerGetMicroservice {
    @Autowired
    private lateinit var beerDAO: BeerDAO

    @RequestMapping(value = ["/getbeerbynamemicroservice/{name}"], method = [RequestMethod.GET])
    @ResponseBody
    fun getBeerByName(@PathVariable name: String): String {
        return beerDAO.getBeerByName(name)!!
    }

    @RequestMapping(value = ["/getbeerbypricemicroservice/{price}"], method = [RequestMethod.GET])
    @ResponseBody
    fun getBeerByPrice(@PathVariable price: Float): String {
        return beerDAO.getBeerByPrice(price)!!
    }

    @RequestMapping(value = ["/getbeersmicroservice"], method = [RequestMethod.GET])
    @ResponseBody
    fun getBeers(): String {
        return beerDAO.getBeers()
    }
}