package org.example.services

import org.example.interfaces.DeleteBeerInterface
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class DeleteBeerService: DeleteBeerInterface {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    private var pattern: Pattern = Pattern.compile("\\W")

    override fun deleteBeer(name: String) {
        if(pattern.matcher(name).find()){
            println("SQL Injection for beer name")
            return
        }
        jdbcTemplate.update("DELETE FROM beers WHERE name = ?", name)
    }
}