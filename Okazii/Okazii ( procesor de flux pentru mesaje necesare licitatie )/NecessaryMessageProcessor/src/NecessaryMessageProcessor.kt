import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException

class NecessaryMessageProcessor{
    private var necessaryMessageSocket: ServerSocket
    private var receiveBidsObservable: Observable<String>
    private val bidderConnections: MutableList<Socket> = mutableListOf()
    private val messageList: MutableList<String> = mutableListOf()

    companion object Constants {
        const val NECESSARYMESSAGE_PORT = 2121
        const val DURATION: Long = 20_000 // licitatia dureaza 15 secunde
    }
    init{
        necessaryMessageSocket = ServerSocket(NECESSARYMESSAGE_PORT)
        necessaryMessageSocket.setSoTimeout(DURATION.toInt())
        println("NecessaryMessageMicroservice se executa pe portul: ${necessaryMessageSocket.localPort}")
        println("Se asteapta mesaje de la bidderi...")

        receiveBidsObservable = Observable.create<String> { emitter ->
            while (true) {
                try {
                    val bidderConnection = necessaryMessageSocket.accept()
                    bidderConnections.add(bidderConnection)

                    // se citeste mesajul de la bidder de pe socketul TCP
                    val bufferReader = BufferedReader(InputStreamReader(bidderConnection.inputStream))
                    val receivedMessage = bufferReader.readLine()

                    // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    if (receivedMessage == null) {
                        // deci subscriber-ul respectiv a fost deconectat
                        bufferReader.close()
                        bidderConnection.close()
                    }

                    // se emite ce s-a citit ca si element in fluxul de mesaje
                    emitter.onNext(receivedMessage)
                } catch (e: SocketTimeoutException) {
                    // daca au trecut cele 15 secunde de la pornirea licitatiei, inseamna ca licitatia s-a incheiat
                    // se emite semnalul Complete pentru a incheia fluxul de oferte
                    emitter.onComplete()
                    break
                }
            }
        }
    }


    private fun receiveBids() {
        // se incepe prin a primi ofertele de la bidderi
        receiveBidsObservable.subscribeBy(
            onNext = {
                val message = Message.deserialize(it.toByteArray())
                messageList.add(message.toString())
                println(message)
            },
            onComplete = {
                write()
            },
            onError = { println("Eroare: $it") }
        )
    }

    fun write() {
        val writer = PrintWriter("../../../file.txt")
        messageList.forEach{ message ->
            writer.append("$message\n")
        }
        writer.close()
    }


    fun run() {
        receiveBids()
    }
}


fun main(args: Array<String>) {
    val necessaryMessageProcessor = NecessaryMessageProcessor()
    necessaryMessageProcessor.run()
}