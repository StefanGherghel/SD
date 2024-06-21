package com.sd.laborator.interfaces

import com.sd.laborator.model.Book

interface LibraryDAO {

    fun createBookTable()
    fun deleteBookTable()
    fun initializeBookTable()
    fun addBook(book: Book)

    fun getBooks(): Set<Book?>
    fun findAllByAuthor(author: String): Set<Book?>
    fun findAllByTitle(title: String): Set<Book?>
    fun findAllByPublisher(publisher: String): Set<Book?>


}