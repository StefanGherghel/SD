package com.sd.laborator.microservices

import com.sd.laborator.interfaces.BeerDAO
import com.sd.laborator.model.Beer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class BeerDeleteMicroservice {
    @Autowired
    private lateinit var beerDAO: BeerDAO

    @RequestMapping(value = ["/deletebeermicroservice/{name}"], method = [RequestMethod.DELETE])
    @ResponseBody
    fun deleteBeer(@PathVariable name: String) {
        beerDAO.deleteBeer(name)
    }
}