package com.sd.laborator.services

import com.sd.laborator.interfaces.CacheDAO
import com.sd.laborator.model.Book
import com.sd.laborator.model.Cache
import com.sd.laborator.model.Content
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import java.io.File
import java.sql.ResultSet
import java.sql.SQLException
import java.util.regex.Pattern

class CacheRowMapper : RowMapper<Cache?> {
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): Cache {
        return Cache(rs.getInt("timestamp"), rs.getString("query"),rs.getString("result"))
    }
}
@Service
class CacheDAOService: CacheDAO {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    var pattern: Pattern = Pattern.compile("\\W")


    override fun getCache(): Set<Cache?> {
        val result: MutableList<Cache?> = jdbcTemplate.query("SELECT * FROM caches", CacheRowMapper())
        return result.toSet()

    }



    override fun addCache(cache: Cache) {
        /*if(pattern.matcher(book.name).find()) {
            println("SQL Injection for book name")
            return
        }*/
        File("/home/adi/modele/ex 9/ex1-tema/LibraryApp/cache.txt").appendText(cache.cacheTimestamp.toString()+"+"+cache.cacheQuery+"+"+cache.cacheResult);
    }

    override fun findByQuery(query: String): Set<Cache?>{
        var result:MutableList<Cache> = mutableListOf()
        var text=File("/home/adi/modele/ex 9/ex1-tema/LibraryApp/cache.txt").readText()
        var caches=text.split("]")
        for (cache in caches) {
            if(cache.length==3) {

                if (cache.split("+")[1] == query) {
                    result.add(Cache(cache.split("+")[0].toInt(), cache.split("+")[1], cache.split("+")[2]))
                    break;
                }
            }
        }
        return result.toSet()
    }

}