package org.example.services

import org.example.interfaces.UpdateBeerInterface
import org.example.model.Beer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class UpdateBeerService:UpdateBeerInterface {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    private var pattern: Pattern = Pattern.compile("\\W")

    override fun updateBeer(beer: Beer) {
        if(pattern.matcher(beer.beerName).find()){
            println("SQL Injection for beer name")
            return
        }
        jdbcTemplate.update("UPDATE beers SET name = ?, price = ? WHERE id = ?", beer.beerName, beer.beerPrice, beer.beerID)
    }
}