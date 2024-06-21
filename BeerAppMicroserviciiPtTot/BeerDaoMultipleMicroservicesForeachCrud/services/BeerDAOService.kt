package com.sd.laborator.services

import com.sd.laborator.interfaces.BeerDAO
import com.sd.laborator.model.Beer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import java.sql.ResultSet
import java.sql.SQLException
import java.util.regex.Pattern

class BeerRowMapper : RowMapper<Beer?> {
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): Beer {
        return Beer(rs.getInt("id"), rs.getString("name"), rs.getFloat("price"))
    }
}

@Service
class BeerDAOService: BeerDAO {
    //Spring Boot will automatically wire this object using application.properties:
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    var pattern: Pattern = Pattern.compile("\\W")

    override fun createBeerTable() {
        jdbcTemplate.execute("""CREATE TABLE IF NOT EXISTS beers(
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        name VARCHAR(100) UNIQUE,
                                        price FLOAT)""")
    }

    override fun addBeer(beer: Beer) {
        if(pattern.matcher(beer.beerName).find()) {
            println("SQL Injection for beer name")
            return
        }
        jdbcTemplate.update("INSERT INTO beers(name, price) VALUES (?, ?)", beer.beerName, beer.beerPrice)
    }

    override fun getBeers(): String {
        val result: MutableList<Beer?> = jdbcTemplate.query("SELECT * FROM beers", BeerRowMapper())
        var stringResult: String = ""
        for (item in result) {
            stringResult += item
        }
        return stringResult
    }

    override fun getBeerByName(name: String): String? {
        if(pattern.matcher(name).find()) {
            println("SQL Injection for beer name")
            return null
        }
        val result: Beer? = jdbcTemplate.queryForObject("SELECT * FROM beers WHERE name = '$name'", BeerRowMapper())
        return result.toString()
    }

    override fun getBeerByPrice(price: Float): String {
        val result: MutableList<Beer?> = jdbcTemplate.query("SELECT * FROM beers WHERE price <= $price", BeerRowMapper())
        var stringResult: String = ""
        for (item in result) {
            stringResult += item
        }
        return stringResult
    }

    override fun updateBeer(beer: Beer) {
        if(pattern.matcher(beer.beerName).find()) {
            println("SQL Injection for beer name")
            return
        }
        jdbcTemplate.update("UPDATE beers SET name = ?, price = ? WHERE id = ?", beer.beerName, beer.beerPrice, beer.beerID)
    }

    override fun deleteBeer(name: String) {
        if(pattern.matcher(name).find()) {
            println("SQL Injection for beer name")
            return
        }
        jdbcTemplate.update("DELETE FROM beers WHERE name = ?", name)
    }
}