package org.example.services

import org.example.interfaces.CreateBeerInterface
import org.example.model.Beer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class CreateBeerService: CreateBeerInterface {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    private var pattern: Pattern = Pattern.compile("\\W")

    override fun createBeerTable() {
        jdbcTemplate.execute("""CREATE TABLE IF NOT EXISTS beers(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name VARCHAR(100) UNIQUE,
                    price FLOAT)""")
    }

    override fun addBeer(beer: Beer) {
        if(pattern.matcher(beer.beerName).find()){
            println("SQL Injection for beer name")
            return
        }
        jdbcTemplate.update("INSERT INTO beers(name, price) VALUES (?, ?)", beer.beerName, beer.beerPrice)
    }
}