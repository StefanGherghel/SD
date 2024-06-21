package logging

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Logging private constructor() {

    private object HOLDER {
        val INSTANCE = Logging()
    }

    companion object {
        val instance: Logging by lazy { HOLDER.INSTANCE }
    }

    fun log(path : String, text : String){
        try{
            val file = File("info.log")
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formatted = current.format(formatter)
            file.appendText("$formatted: $text\n")
        } catch (e : Throwable) {
            println("Could not write to file ${e.message}")
        }
    }
}

fun main(args: Array<String>){
    val logger = Logging.instance
    logger.log("test.log", "Hello");
}