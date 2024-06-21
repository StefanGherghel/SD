import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import logging.Logging
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.*
import kotlin.Exception
import kotlin.system.exitProcess

class AuctioneerMicroservice{
    private var auctioneerSocket: ServerSocket
    private lateinit var messageProcessorSocket: Socket
    private var receiveBidsObservable: Observable<String>
    private val subscriptions = CompositeDisposable()
    private val bidQueue: Queue<Message> = LinkedList()
    private val bidderConnections: MutableList<Socket> = mutableListOf()

    companion object Constants{
        const val MESSAGE_PROCESSOR_HOST = "localhost"
        const val MESSAGE_PROCESSOR_PORT = 1_600
        const val AUCTIONEER_PORT = 1_500
        const val AUCTION_DURATION: Long = 15_000 //licitatia dureaza 15 secunde
        const val LOG_PATH = "auctioneer.log"
        val logger = Logging.instance
    }

    init {
        auctioneerSocket = ServerSocket(AUCTIONEER_PORT)
        auctioneerSocket.soTimeout = AUCTION_DURATION.toInt()
        println("[AuctioneerMicroservice]: se executa pe portul: ${auctioneerSocket.localPort}")
        println("[AuctioneerMicroservice]: Se asteapta oferte de la bidderi...")
        logger.log(LOG_PATH, "[AuctioneerMicroservice]: se executa pe portul: ${auctioneerSocket.localPort}")
        logger.log(LOG_PATH, "[AuctioneerMicroservice]: Se asteapta oferte de la bidderi...")

        // Se creeaza obiectul Observable cu care se genereaza evenimente cand se primesc oferte de la bidderi
        receiveBidsObservable = Observable.create<String>{
                emitter ->
            // Se asteapta conexiuni din partea bidderilor
            while (true){
                try{
                    val bidderConnection = auctioneerSocket.accept()
                    bidderConnections.add(bidderConnection)

                    // Se citeste mesajul de la bidder de pe socketul TCP
                    val bufferReader = BufferedReader(InputStreamReader(bidderConnection.inputStream))
                    val receiveMessage = bufferReader.readLine()

                    //Daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    if(receiveMessage == null){
                        //deci subscriber-ul respectiv a fost deconectat
                        bufferReader.close()
                        bidderConnection.close()

                        emitter.onError(Exception("[AuctioneerMicroservice]: Eroare: Bidderul ${bidderConnection.port} a fost deconectat."))
                    }
                    //Se emite ce s-a citit ca si element in fluxul de mesaje

                    emitter.onNext(receiveMessage)
                }catch (e: SocketTimeoutException){
                    //Daca au trecut cele 15 secunde de la pornirea licitatiei, inseamna ca licitatia s-a incheiat
                    // Se emite semnalul Complete pentru a incheia fluxul de oferte
                    emitter.onComplete()
                    break
                }
            }
        }
    }

    private fun receiveBids(){
        //Se incepe prin a primi ofertele de la bidderi
        val receiveBidsSubscription = receiveBidsObservable.subscribeBy(
            onNext = {
                val message = Message.deserialize(it.toByteArray())
                println("[AuctioneerMicroservice]: $message")
                logger.log(LOG_PATH, "[AuctioneerMicroservice]: $message")
                bidQueue.add(message)
            },
            onComplete = {
                //Licitatia s-a incheiat
                //Se trimit raspunsurile mai departe catre procesorul de mesaje

                println("[AuctioneerMicroservice]: Licitatia s-a incheiat! Se trimit ofertele spre procesare...")
                logger.log(LOG_PATH, "[AuctioneerMicroservice]: Licitatia s-a incheiat! Se trimit ofertele spre procesare...")
                forwardBids()
            },
            onError = {
                println("[AuctioneerMicroservice]: Eroare: ${it.stackTraceToString()}")
                logger.log(LOG_PATH, "[AuctioneerMicroservice]: Eroare: $it")
            }
        )
        subscriptions.add(receiveBidsSubscription)
    }

    private fun forwardBids(){
        try{
            messageProcessorSocket = Socket(MESSAGE_PROCESSOR_HOST, MESSAGE_PROCESSOR_PORT)
            subscriptions.add(Observable.fromIterable(bidQueue).subscribeBy(
                onNext = {
                    // Trimitere mesaje catre procesorul de mesaje
                    messageProcessorSocket.getOutputStream().write(it.serialize())
                    println("[AuctioneerMicroservice]: Am trimis mesajul: $it")
                    logger.log(LOG_PATH, "[AuctioneerMicroservice]: Am trimis mesajul: $it")
                },
                onComplete = {

                    println("[AuctioneerMicroservice]: Am trimis toate ofertele catre MessageProcessor.")
                    logger.log(LOG_PATH, "[AuctioneerMicroservice]: Am trimis toate ofertele catre MessageProcessor.")
                    val bidEndMessage = Message.create("${messageProcessorSocket.localAddress}:${messageProcessorSocket.localPort}", "final")
                    messageProcessorSocket.getOutputStream().write(bidEndMessage.serialize())

                    //Dupa ce s-a terminat licitatia, se asteapta raspuns de la MessageProcessorMicroservice
                    //Cum ca a primit toate mesajele
                    val bufferreader = BufferedReader(InputStreamReader(messageProcessorSocket.inputStream))
                    bufferreader.readLine()
                    messageProcessorSocket.close()
                    finishAuction()
                }
            ))
        }catch (e: Exception){
            println("[AuctioneerMicroservice]: Nu ma pot conecta la MessageProcessor!")
            logger.log(LOG_PATH, "[AuctioneerMicroservice]: Nu ma pot conecta la MessageProcessor!")
            auctioneerSocket.close()
            exitProcess(1)
        }
    }

    private fun finishAuction() {
        // se asteapta rezultatul licitatiei
        try {
            val biddingProcessorConnection = auctioneerSocket.accept()
            val bufferReader = BufferedReader(InputStreamReader(biddingProcessorConnection.inputStream))
            // se citeste rezultatul licitatiei de la AuctioneerMicroservice de pe socketul TCP
            val receivedMessage = bufferReader.readLine()
            val result: Message = Message.deserialize(receivedMessage.toByteArray())
            val winningPrice = result.body.split(":")[1].toInt()
            println("[AuctioneerMicroservice]: Am primit rezultatul licitatiei de la BiddingProcessor: ${result.sender} a castigat cu pretul:$winningPrice")
            logger.log(LOG_PATH,
                "[AuctioneerMicroservice]: Am primit rezultatul licitatiei de la BiddingProcessor: ${result.sender} a castigat cu pretul:$winningPrice")

            // se creeaza mesajele pentru rezultatele licitatiei

            // se anunta castigatorul
            bidderConnections.forEach {
                if (it.remoteSocketAddress.toString() == result.sender) {
                    it.getOutputStream().write("winner\n".toByteArray())
                } else {
                    it.getOutputStream().write("loser\n".toByteArray())
                }
                it.close()
            }
        } catch (e: Exception) {
            println("[AuctioneerMicroservice]: Nu ma pot conecta la BiddingProcessor!")
            logger.log(LOG_PATH, "[AuctioneerMicroservice]: Nu ma pot conecta la BiddingProcessor!")
            auctioneerSocket.close()
            exitProcess(1)
        }
        // se elibereaza memoria din multimea de Subscriptions
        subscriptions.dispose()
    }

    fun run() {
        receiveBids()
    }
}

fun main(args: Array<String>) {
    val bidderMicroservice = AuctioneerMicroservice()
    bidderMicroservice.run()
}