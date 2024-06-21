import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import java.util.*
import java.util.concurrent.Flow
import kotlin.Exception
import kotlin.concurrent.timer
import kotlin.math.abs
import kotlin.random.Random
import kotlin.system.exitProcess

class BidderMicroservice {
    private var auctioneerSocket: Socket
    private var auctionResultObservable: Observable<String>
    private var myIdentity: String = "[BIDDER_NECONECTAT]"
    private var loggerSocket : Socket

    companion object Constants {
        const val AUCTIONEER_HOST = "localhost"
        const val AUCTIONEER_PORT = 1500

        const val MAX_BID = 10_000
        const val MIN_BID = 1_000

        const val LOGGER_HOST = "localhost"
        const val LOGGER_PORT = 2000

        private val firstNameDict = hashMapOf<Int, String>(
            1 to "Cojocaru",
            2 to "Alexandrescu",
            3 to "Popescu",
            4 to "Baciu",
            5 to "Grosu",
            6 to "Dascalu",
            7 to "Botezatu",
            8 to "Irimescu",
            9 to "Cretu",
            10 to "Danciu"
        )

        private val lastNameDict = hashMapOf<Int, String>(
            1 to "Cosmin",
            2 to "Radu",
            3 to "Mihai",
            4 to "Ion",
            5 to "Teodor",
            6 to "Stefan",
            7 to "Bogdan",
            8 to "Andrei",
            9 to "Vlad",
            10 to "Catalin"
        )

        private fun generateBidder() : String
        {
            val random = java.util.Random(Date().time)
            val generateFirstNumber : Int = random.nextInt(firstNameDict.size) + 1
            val generateLastNumber : Int = random.nextInt(firstNameDict.size) + 1

            val bidderName = firstNameDict[generateFirstNumber] + " " + lastNameDict[generateLastNumber]
            val bidderEmail = firstNameDict[generateFirstNumber]!!.toLowerCase() + "_" + lastNameDict[generateLastNumber]!!.toLowerCase() + "@gmail.com"
            val phoneNumber =  "+407" + (abs(random.nextInt())).toString()

            return "($bidderName, $phoneNumber, $bidderEmail)" //"($bidderName $phoneNumber $bidderEmail)"
        }

    }

    init {
        try {
            loggerSocket = Socket(LOGGER_HOST, LOGGER_PORT)
            //myIdentity = Bidder.generateBidder().toString()
                myIdentity = generateBidder()

            auctioneerSocket = Socket(AUCTIONEER_HOST, AUCTIONEER_PORT)

            println("M-am conectat la Auctioneer!")

            // se creeaza un obiect Observable ce va emite mesaje primite printr-un TCP
            // fiecare mesaj primit reprezinta un element al fluxului de date reactiv

            auctionResultObservable = Observable.create<String> { emitter ->
                // se citeste raspunsul de pe socketul TCP

                val bufferReader = BufferedReader(InputStreamReader(auctioneerSocket.inputStream))
                val receivedMessage = bufferReader.readLine()

                // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                if (receivedMessage == null) {
                    bufferReader.close()
                    auctioneerSocket.close()

                    emitter.onError(Exception("AuctioneerMicroservice s-a deconectat."))
                    return@create
                }

                // mesajul primit este emis in flux
                emitter.onNext(receivedMessage)

                // deoarece se asteapta un singur mesaj, in continuare se emite semnalul de incheiere al fluxului
                emitter.onComplete()

                bufferReader.close()
                auctioneerSocket.close()
            }
        } catch (e: Exception) {
            println("$myIdentity Nu ma pot conecta la Auctioneer!")
            exitProcess(1)
        }

    }

    private fun bid() {
        // se genereaza o oferta aleatorie din partea bidderului curent
        val pret = Random.nextInt(MIN_BID, MAX_BID)

        // se creeaza mesajul care incapsuleaza oferta ${auctioneerSocket.localAddress}:${auctioneerSocket.localPort}
        val biddingMessage = Message.create(myIdentity,
            "liciteaza $pret")

        // bidder-ul trimite pretul pentru care doreste sa liciteze
        val serializedMessage = biddingMessage.serialize()
        auctioneerSocket.getOutputStream().write(serializedMessage)

        // exista o sansa din 2 ca bidder-ul sa-si trimita oferta de 2 ori, eronat
        if (Random.nextBoolean()) {
            auctioneerSocket.getOutputStream().write(serializedMessage)
        }
    }

    private fun waitForResult() {
        println("$myIdentity Astept rezultatul licitatiei...")
        // bidder-ul se inscrie pentru primirea unui raspuns la oferta trimisa de acesta

        val auctionResultSubscription = auctionResultObservable.subscribeBy(
            // cand se primeste un mesaj in flux, inseamna ca a sosit rezultatul licitatiei
            onNext = {
                val resultMessage: Message = Message.deserialize(it.toByteArray())
                println("$myIdentity Rezultat licitatie: ${resultMessage.body}")

                // Se evalueaza serviciul dupa primirea rezultatului
                val random = java.util.Random(Date().time)
                val grade : Int = random.nextInt(4) + 1
                loggerSocket.getOutputStream().write("Persoana $myIdentity a dat nota $grade".toByteArray())
            },
            onError = {
                println("$myIdentity Eroare: $it")
            }
        )

        // se elibereaza memoria obiectului Subscription
        auctionResultSubscription.dispose()
    }

    fun run() {
        bid()
        waitForResult()
    }
}

fun main(args: Array<String>) {
    val bidderMicroservice = BidderMicroservice()
    bidderMicroservice.run()
}