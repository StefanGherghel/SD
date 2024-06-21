package com.sd.laborator.microservices

import com.sd.laborator.model.Beer
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

// fuser -k 8080/tcp

@Controller
class BeerCRUDMicroservice {
    @RequestMapping(value = ["/getbeers"], method = [RequestMethod.GET])
    @ResponseBody
    fun getBeers(): String {
        return khttp.get("http://localhost:8080/getbeersmicroservice").text
    }

    @RequestMapping(value = ["/getbeerbyname/{name}"], method = [RequestMethod.GET])
    @ResponseBody
    fun getBeerByName(@PathVariable name: String): String {
        return khttp.get("http://localhost:8080/getbeerbynamemicroservice/$name").text
    }

    @RequestMapping(value = ["/getbeerbyprice/{price}"], method = [RequestMethod.GET])
    @ResponseBody
    fun getBeerByPrice(@PathVariable price: Float): String {
        return khttp.get("http://localhost:8080/getbeerbypricemicroservice/$price").text
    }

    @RequestMapping(value = ["/addbeer"], method = [RequestMethod.POST])
    @ResponseBody
    fun addBeer(@RequestParam beer: Map<String, String>) {
        khttp.post("http://localhost:8080/addbeermicroservice", data = beer)
    }

    @RequestMapping(value = ["/updatebeer"], method = [RequestMethod.PUT])
    @ResponseBody
    fun updateBeer(@RequestParam beer: Map<String, String>) {
        khttp.put("http://localhost:8080/updatebeermicroservice", data = beer)
    }

    @RequestMapping(value = ["/deletebeer/{name}"], method = [RequestMethod.DELETE])
    @ResponseBody
    fun deleteBeer(@PathVariable name: String) {
        khttp.delete("http://localhost:8080/deletebeermicroservice/$name")
    }
}