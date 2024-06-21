import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import logging.Logging
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import kotlin.Exception
import kotlin.random.Random
import kotlin.system.exitProcess

class BidderMicroservice {

    private var auctioneerSocket: Socket
    private var chatSocket: Socket
    private var auctioneerResultObservable: Observable<String>
    private var chatMessagesObservable: Observable<String>
    private var chatMessagesSendObservable: Observable<String>

    private var myIdentity: String = "[BIDDER_NECONECTAT]"

    private val names: Set<String> = setOf(
        "Gigel",
        "Bula",
        "Andrei",
        "Andreea",
        "Bianca",
        "Gogu",
        "Marian",
        "Pete",
        "George"
    )

    private val emails: Set<String> = setOf(
        "gmail.com",
        "yahoo.ro",
        "student.tuiasi.ro",
        "tuiasi.ro",
        "abc.com"
    )

    companion object Constants{
        const val AUCTIONEER_HOST = "localhost"
        const val AUCTIONEER_PORT = 1500
        const val CHAT_HOST = "localhost"
        const val CHAT_PORT = 8888
        const val MAX_BID = 10_000
        const val MIN_BID = 1_000
        const val LOG_PATH = "bidder.log"
        val logger = Logging.instance
    }

    init{
        try{
            auctioneerSocket = Socket(AUCTIONEER_HOST, AUCTIONEER_PORT)
            chatSocket = Socket(CHAT_HOST, CHAT_PORT)
            println("[BidderMicroservice]: M-am conectat la Auctioneer!")
            logger.log(LOG_PATH, "[BidderMicroservice]: ${auctioneerSocket.localPort} M-am conectat la Auctioneer!")
            println("[BidderMicroservice]: M-am conectat la Chat!")
            logger.log(LOG_PATH, "[BidderMicroservice]: ${auctioneerSocket.localPort} M-am conectat la Chat!")
            myIdentity = "[${auctioneerSocket.localPort}]"

            chatMessagesSendObservable = Observable.create {
                emitter->
                try {
                    val msg = readLine()!!.toString()
                    emitter.onNext(msg)
                }
                catch (e: Throwable){
                    emitter.onError(e)
                }
            }

            chatMessagesObservable = Observable.create {
                emitter ->
                val bufferedReader = BufferedReader(InputStreamReader(chatSocket.getInputStream()))
                val receivedMessage = bufferedReader.readLine()

                if (receivedMessage == "gata"){
                    bufferedReader.close()
                    chatSocket.close()
                    emitter.onComplete()
                }

                if(receivedMessage == null){
                    bufferedReader.close()
                    chatSocket.close()
                    emitter.onError(Exception("ChatMicroservice s-a deconectat"))
                    return@create
                }
                // Mesajul primit este emis in flux
                emitter.onNext(receivedMessage)
            }

            // Se creeaza un obiect Observable ce va emite mesage primite printr-un TCP
            // Fiecare mesaj primit reprezinta un element al fluxului de de date reactiv
            auctioneerResultObservable = Observable.create {
                    emitter ->
                // Se citeste raspunsul de pe socketul TCP
                val bufferedReader = BufferedReader(InputStreamReader(auctioneerSocket.getInputStream()))
                val receivedMessage = bufferedReader.readLine()

                // Daca se primeste un mesaj gol(NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                if(receivedMessage == null){
                    bufferedReader.close()
                    auctioneerSocket.close()
                    emitter.onError(Exception("AuctioneerMicroservice s-a deconectat"))
                    return@create
                }
                // Mesajul primit este emis in flux
                emitter.onNext(receivedMessage)
                // Deoarece se asteapta un sungur mesaj, in continuare se emite semnalul de incheiere al fluxului
                emitter.onComplete()

                bufferedReader.close()
                auctioneerSocket.close()
            }
        }catch (e: Exception){
            println("[BidderMicroservice]: $myIdentity Nu ma pot conecta la Auctioneer!")
            logger.log(LOG_PATH, "[BidderMicroservice]: $myIdentity Nu ma pot conecta la Auctioneer!")
            exitProcess(1)
        }
    }

    private fun bid(){
        val pret = Random.nextInt(MIN_BID, MAX_BID)
        val name = names.random()
        val biddingMessage = Message.create(
                sender="${auctioneerSocket.localAddress}:${auctioneerSocket.localPort}",
                body="licitez:$pret",
                telephone = "072" + Random.nextInt(1_000_000, 9_999_999),
                name = names.random(),
                email = name + "@" + emails.random()
        )

        // Bidderul trimite pretul pentru care doreste sa liciteze
        auctioneerSocket.getOutputStream().write(biddingMessage.serialize())

        // Exista o sansa din 2 ca bidderul sa-si trimita oferta de 2 ori eronat
        if(Random.nextBoolean()){
            auctioneerSocket.getOutputStream().write(biddingMessage.serialize())
        }
    }

    private fun waitForResult(){
        println("[BidderMicroservice]: $myIdentity Astept rezultatul licitatiei...")
        logger.log(LOG_PATH, "[BidderMicroservice]: $myIdentity Astept rezultatul licitatiei...")
        // Bidder-ul se inscrie pentru primirea unui raspuns la oferta trimisa de acesta
        val auctionResultSubscription = auctioneerResultObservable.subscribeBy(
            //cand se primeste un mesaj in flux, inseamna ca a sosit rezultatul licitatiei
            onNext = {
                // val resultMessage: Message = Message.deserialize(it.toByteArray())
                println("[BidderMicroservice]: $myIdentity Rezultat licitatie:${it}")
                logger.log(LOG_PATH, "[BidderMicroservice]: $myIdentity Rezultat licitatie:${it}")
            },
            onError = {
                println("[BidderMicroservice]: $myIdentity Eroare: $it")
                logger.log(LOG_PATH, "[BidderMicroservice]: $myIdentity Eroare: $it")
            }
        )
        // Se elibereaza memoria obiectului Subscription
        auctionResultSubscription.dispose()
    }

    private fun chat(){
        println("[BidderMicroservice]: $myIdentity Astept mesaje de pe chat...")
        logger.log(LOG_PATH, "[BidderMicroservice]: $myIdentity Astept rezultatul licitatiei...")
        // Bidder-ul se inscrie pentru primirea unui raspuns la oferta trimisa de acesta
        val chatMessagesReceived = chatMessagesObservable.subscribeBy(
                //cand se primeste un mesaj in flux, inseamna ca a sosit rezultatul licitatiei
                onNext = {
                    println("[BidderMicroservice-Chatlog]: $it.")
                    logger.log(LOG_PATH, "[BidderMicroservice-Chatlog]: $it.")
                },
                onComplete = {
                    println("[BidderMicroservice]: $myIdentity Chatul a fost inchis.")
                    logger.log(LOG_PATH, "[BidderMicroservice]: $myIdentity Chatul a fost inchis.")
                },
                onError = {
                    println("[BidderMicroservice]: $myIdentity Eroare: $it")
                    logger.log(LOG_PATH, "[BidderMicroservice]: $myIdentity Eroare: $it")
                }
        )
        val chatMessagesSent = chatMessagesSendObservable.subscribeBy(
                onNext = {
                    chatSocket.getOutputStream().write("$it\n".toByteArray())
                    println("[BidderMicroservice-Chatlog]: $myIdentity $it.")
                    logger.log(LOG_PATH, "[BidderMicroservice-Chatlog]: $myIdentity $it.")
                },
                onComplete = {
                    println("[BidderMicroservice]: $myIdentity Chat-ul este inchis si nu se mai pot trimite mesaje.")
                    logger.log(LOG_PATH, "[BidderMicroservice]: $myIdentity Chat-ul este inchis si nu se mai pot trimite mesaje.")
                },
                onError = {
                    println("[BidderMicroservice]: $myIdentity Eroare: $it")
                    logger.log(LOG_PATH, "[BidderMicroservice]: $myIdentity Eroare: $it")
                }
        )
        // Se elibereaza memoria obiectului Subscription
        chatMessagesReceived.dispose()
    }

    fun run(){
        bid()
        chat()
        waitForResult()
    }
}

fun main(args: Array<String>){
    val bidderMicroservice = BidderMicroservice()
    bidderMicroservice.run()
}