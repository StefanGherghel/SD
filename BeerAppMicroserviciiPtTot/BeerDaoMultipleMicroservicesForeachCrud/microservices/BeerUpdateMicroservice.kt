package com.sd.laborator.microservices

import com.sd.laborator.interfaces.BeerDAO
import com.sd.laborator.model.Beer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class BeerUpdateMicroservice {
    @Autowired
    private lateinit var beerDAO: BeerDAO

    @RequestMapping(value = ["/updatebeermicroservice"], method = [RequestMethod.PUT])
    @ResponseBody
    fun updateBeer(@RequestParam beer: Map<String, String>) {
        beerDAO.updateBeer(
            Beer(
                    id = beer["id"]?.toInt()!!,
                    name = beer["name"].toString(),
                    price = beer["price"]?.toFloat()!!
            ))
    }
}