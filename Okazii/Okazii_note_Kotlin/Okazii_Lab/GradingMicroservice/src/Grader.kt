import io.reactivex.rxjava3.core.Observable
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread


class GradingMicroservice {
    private var gradingSocket = ServerSocket()

    private var socketsConnected = 0
    private var socketsCounterLock : ReentrantLock = ReentrantLock()
    private var logLock : ReentrantLock = ReentrantLock()


    companion object Constants {
        const val LOGGER_PORT = 2000
        const val FILE_PATH = "/home/cosmin29/result.txt"
    }

    init {

        val file = File(FILE_PATH)
        if(!file.exists())
            file.createNewFile()
        println("Fisierul este deschis in: " + FILE_PATH)

        gradingSocket = ServerSocket(LOGGER_PORT)
        logLock.lock()
        logLock.unlock()
        println("GradingMicroservice se executa pe portul: ${gradingSocket.localPort}")
        println("Se asteapta evaluari...")

        // se asteapta mesaje primite de la alte microservicii



        try {

            while (true) {

                val senderSocket = gradingSocket.accept()

                thread { startMicroserviceConnection(senderSocket) }

                socketsCounterLock.lock()
                socketsConnected++
                socketsCounterLock.unlock()
            }
        } catch (se: SocketException) {
            logLock.lock()
            if(socketsConnected == 0)
                println("Termianre executie grader...")
            else
                println("Eroare in GradingMicroservice.")
            logLock.unlock()
        }

        println("Am iesit din grader.")
    }

    private fun startMicroserviceConnection(senderSocket : Socket) {


            try {
                val bufferReader = BufferedReader(InputStreamReader(senderSocket.inputStream))
                while (true) {
                    var receivedMessage = bufferReader.readLine()
                    println("Mesajul primit este: $receivedMessage")

                    if (receivedMessage == null) {
                        bufferReader.close()
                        break
                    }

                    logLock.lock()
                    Files.write(Paths.get(FILE_PATH), (receivedMessage + "\n").toByteArray(), StandardOpenOption.APPEND)
                    logLock.unlock()

                }
            }
            catch (ex : Exception)
            {
                logLock.lock()
                println("Exceptie in GradingMicroservice: " + ex.stackTrace + "\n\n")
                logLock.unlock()
            }
        finally {
            if(logLock.isLocked)
                logLock.unlock()

            if(socketsCounterLock.isLocked)
                socketsCounterLock.unlock()

            senderSocket.close()
        }

        socketsCounterLock.lock()
        socketsConnected--
        socketsCounterLock.unlock()

        println("Ies din thread. Thread-uri ramase: $socketsConnected")

        if(socketsConnected == 0)
            this.gradingSocket.close()

    }
}

fun main(args: Array<String>) {
    val gradingMicroservice = GradingMicroservice()
}