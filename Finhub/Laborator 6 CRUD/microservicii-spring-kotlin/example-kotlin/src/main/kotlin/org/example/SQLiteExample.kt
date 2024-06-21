package org.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class SQLiteExample

fun main(args: Array<String>) {
    runApplication<SQLiteExample>(*args)
}