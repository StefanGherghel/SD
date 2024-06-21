package org.example.services

import org.example.interfaces.GetBeerInterface
import org.example.model.Beer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class GetBeerService: GetBeerInterface {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    private var pattern: Pattern = Pattern.compile("\\W")

    override fun getBeers(): String {
        val result: MutableList<Beer?> = jdbcTemplate.query("SELECT * FROM beers", BeerRowMapper())
        var stringResult: String = ""
        result.forEach { stringResult += it }
        return stringResult
    }

    override fun getBeerByName(name: String): String? {
        if(pattern.matcher(name).find()){
            println("SQL Injection for beer name")
            return null
        }
        val result: Beer? = jdbcTemplate.queryForObject("SELECT * FROM beers WHERE name = '$name'", BeerRowMapper())
        return result.toString()
    }

    override fun getBeerByPrice(price: Float): String? {
        val result: MutableList<Beer?> = jdbcTemplate.query("SELECT * FROM beers WHERE price <= $price", BeerRowMapper())
        var stringResult: String = ""
        result.forEach{ stringResult += it}
        return stringResult
    }
}