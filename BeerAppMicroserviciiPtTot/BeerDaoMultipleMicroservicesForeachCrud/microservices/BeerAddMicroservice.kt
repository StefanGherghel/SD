package com.sd.laborator.microservices

import com.sd.laborator.interfaces.BeerDAO
import com.sd.laborator.model.Beer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class BeerAddMicroservice {
    @Autowired
    private lateinit var beerDAO: BeerDAO

    @RequestMapping(value = ["/addbeermicroservice"], method = [RequestMethod.POST])
    @ResponseBody
    fun addBeer(@RequestParam beer: Map<String, String>) {
        beerDAO.addBeer(
            Beer(
                    id = -1,
                    name = beer["name"].toString(),
                    price = beer["price"]?.toFloat()!!
            ))
    }
}