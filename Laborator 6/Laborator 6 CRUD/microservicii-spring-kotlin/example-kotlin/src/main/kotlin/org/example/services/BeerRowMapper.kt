package org.example.services

import org.example.model.Beer
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.jvm.Throws

class BeerRowMapper: RowMapper<Beer?> {
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): Beer {
        return Beer(rs.getInt("id"), rs.getString("name"), rs.getFloat("price"))
    }
}