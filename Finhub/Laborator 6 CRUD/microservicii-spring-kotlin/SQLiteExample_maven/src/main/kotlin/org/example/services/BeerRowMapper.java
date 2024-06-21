package org.example.services;

import kotlin.jvm.Throws;

import java.sql.SQLException;

class BeerRowMapper{

}
/*
class BeerRowMapper: RowMapper<Beer?>{
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): Beer {
        return Beer(rs.getInt("id"), rs.getString("name"), rs.getFloat("price"))
    }
}
 */