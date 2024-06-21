package com.sd.laborator.services

import com.sd.laborator.interfaces.LibraryDAO
import com.sd.laborator.model.Book
import com.sd.laborator.model.Content
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import java.sql.ResultSet
import java.sql.SQLException
import java.util.regex.Pattern

class BookRowMapper : RowMapper<Book?> {
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): Book {
        return Book(Content(rs.getInt("id"), rs.getString("author"), rs.getString("text"),rs.getString("title"),rs.getString("publisher")))
    }
}

@Service
class LibraryDAOService: LibraryDAO {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    var pattern: Pattern = Pattern.compile("\\W")

    override fun createBookTable() {
        jdbcTemplate.execute("""CREATE TABLE IF NOT EXISTS books(
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        author VARCHAR(100),
                                        title VARCHAR(100),
                                        publisher VARCHAR(100),
                                        text TEXT,
                                        CONSTRAINT constrangere UNIQUE (author,title,publisher,text))""")
        /*addBook(Book(Content(1,"Roberto Ierusalimschy","Preface. When Waldemar, Luiz, and I started the development of Lua, back in 1993, we could hardly imagine that it would spread as it did. ...","Programming in LUA","Teora")))
        addBook(Book(Content(2,"Jules Verne","Nemaipomeniti sunt francezii astia! - Vorbiti, domnule, va ascult! ....","Steaua Sudului","Corint")))
        addBook(Book(Content(3,"Jules Verne","Cuvant Inainte. Imaginatia copiilor - zicea un mare poet romantic spaniol - este asemenea unui cal nazdravan, iar curiozitatea lor e pintenul ce-l fugareste prin lumea celor mai indraznete proiecte.","O calatorie spre centrul pamantului","Polirom")))
        addBook(Book(Content(4,"Jules Verne","Partea intai. Naufragiatii vazduhului. Capitolul 1. Uraganul din 1865. ...","Insula Misterioasa","Teora")))
        addBook(Book(Content(5,"Jules Verne","Capitolul I. S-a pus un premiu pe capul unui om. Se ofera premiu de 2000 de lire ...","Casa cu aburi","Albatros")))
        */
    }
    override fun deleteBookTable()
    {
        jdbcTemplate.execute("""DROP TABLE books""")
    }
    override fun initializeBookTable()
    {
        addBook(Book(Content(1,"Roberto Ierusalimschy","Preface. When Waldemar, Luiz, and I started the development of Lua, back in 1993, we could hardly imagine that it would spread as it did. ...","Programming in LUA","Teora")))
        addBook(Book(Content(2,"Jules Verne","Nemaipomeniti sunt francezii astia! - Vorbiti, domnule, va ascult! ....","Steaua Sudului","Corint")))
        addBook(Book(Content(3,"Jules Verne","Cuvant Inainte. Imaginatia copiilor - zicea un mare poet romantic spaniol - este asemenea unui cal nazdravan, iar curiozitatea lor e pintenul ce-l fugareste prin lumea celor mai indraznete proiecte.","O calatorie spre centrul pamantului","Polirom")))
        addBook(Book(Content(4,"Jules Verne","Partea intai. Naufragiatii vazduhului. Capitolul 1. Uraganul din 1865. ...","Insula Misterioasa","Teora")))
        addBook(Book(Content(5,"Jules Verne","Capitolul I. S-a pus un premiu pe capul unui om. Se ofera premiu de 2000 de lire ...","Casa cu aburi","Albatros")))
    }
    override fun getBooks(): Set<Book?> {
        val result: MutableList<Book?> = jdbcTemplate.query("SELECT * FROM books", BookRowMapper())
        return result.toSet()

    }



    override fun addBook(book: Book) {
        /*if(pattern.matcher(book.name).find()) {
            println("SQL Injection for book name")
            return
        }*/
        jdbcTemplate.update("INSERT INTO books(author,text,title,publisher ) VALUES (?, ?,?,?)", book.author, book.content,book.name,book.publisher)
    }

    override fun findAllByAuthor(author: String): Set<Book?> {
        val result: MutableList<Book?> = jdbcTemplate.query("SELECT * FROM books WHERE author = '$author'", BookRowMapper())
        return result.toSet()
    }

    override fun findAllByTitle(title: String): Set<Book?> {

        val result: MutableList<Book?> = jdbcTemplate.query("SELECT * FROM books WHERE title = '$title'", BookRowMapper())
        return result.toSet()
    }

    override fun findAllByPublisher(publisher: String): Set<Book?> {
        val result: MutableList<Book?> = jdbcTemplate.query("SELECT * FROM books WHERE publisher = '$publisher'", BookRowMapper())
        return result.toSet()
    }
}