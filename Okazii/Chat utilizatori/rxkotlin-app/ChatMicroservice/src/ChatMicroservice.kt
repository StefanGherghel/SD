import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.net.ServerSocket
import java.net.Socket
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import logging.Logging
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.SocketTimeoutException

class ChatMicroservice {
    private var chatSocket: ServerSocket
    private var receiveUsersObservable: Observable<Pair<Socket, Message>>
    private var receiveMessageObservable: Observable<String>
    private val subscriptions = CompositeDisposable()
    private val bidderConnections: MutableList<Socket> = mutableListOf()
    private val bidderInfo: MutableList<Message> = mutableListOf()
    private val logger = Logging.instance

    companion object{
        const val DURATION: Long = 15_000
        const val CHAT_PORT = 8888
        const val LOG_PATH = ""
    }

    init {
        chatSocket = ServerSocket(CHAT_PORT)
        chatSocket.soTimeout = DURATION.toInt()
        println("[ChatMicroservice]: Chat-ul se executa pe portul: ${chatSocket.localPort}")
        println("[ChatMicroservice]: Se asteapta mesaje de la bidderi...")
        logger.log(LOG_PATH, "[ChatMicroservice]: se executa pe portul: ${chatSocket.localPort}")
        logger.log(LOG_PATH, "[ChatMicroservice]: Se asteapta mesaje de la bidderi...")

        receiveMessageObservable = Observable.create {
            emitter->
            try {
                while (true) {
                    if (chatSocket.isClosed)
                    {
                        emitter.onComplete()
                    }
                    bidderConnections.forEach {
                        if (it.getInputStream().available() != 0) {
                            val bufferReader = BufferedReader(InputStreamReader(it.inputStream))
                            val receiveMessage = bufferReader.readLine()
                            emitter.onNext(receiveMessage)
                        }
                    }
                }
            }
            catch (e: Throwable){
                emitter.onError(e)
            }
        }

        receiveUsersObservable = Observable.create {
            emitter->
            while (true){
                try{
                    val bidderConnection = chatSocket.accept()
                    if(bidderConnection == null){
                        //deci subscriber-ul respectiv a fost deconectat
                        emitter.onError(Exception("[ChatMicroservice]: Eroare: Bidderul a fost deconectat."))
                    }

                    // Se citeste mesajul de la bidder de pe socketul TCP
                    val bufferReader = BufferedReader(InputStreamReader(bidderConnection.inputStream))
                    val receiveMessage = bufferReader.readLine()

                    //Daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    if(receiveMessage == null){
                        //deci subscriber-ul respectiv a fost deconectat
                        bufferReader.close()
                        bidderConnection.close()

                        emitter.onError(Exception("[ChatMicroservice]: Eroare: Bidderul ${bidderConnection.port} a fost deconectat."))
                    }
                    emitter.onNext(Pair(bidderConnection, Message.deserialize(receiveMessage.toByteArray())))
                }catch (e: SocketTimeoutException){
                    //Daca au trecut cele 15 secunde de la pornirea licitatiei, inseamna ca licitatia s-a incheiat
                    // Se emite semnalul Complete pentru a incheia fluxul de oferte
                    emitter.onComplete()
                    break
                }
            }
        }
    }

    private fun saveBidders(){
        val disposable = receiveUsersObservable.subscribeBy(
                onNext = {
                    println("[ChatMicroservice]: Am primit userul: ${it.second}")
                    logger.log(LOG_PATH, "[ChatMicroservice]: Am primit userul: ${it.second}")
                    bidderConnections.add(it.first)
                    bidderInfo.add(it.second)
                },
                onComplete = {
                    println("[ChatMicroservice]: Au fost conectati toti userii la licitatie.")
                    logger.log(LOG_PATH, "[ChatMicroservice]: Au fost conectati toti userii la licitatie.")
                    bidderConnections.forEach {
                        it.getOutputStream().write("Lista Useri:\n".toByteArray())
                        it.getOutputStream().write("${bidderInfo}\n".toByteArray())
                    }
                },
                onError = {
                    println("[AuctioneerMicroservice]: Eroare: ${it.stackTraceToString()}")
                    logger.log(LOG_PATH, "[AuctioneerMicroservice]: Eroare: ${it.stackTraceToString()}")
                }
        )
        subscriptions.add(disposable)
    }

    private fun runChat(){
        val disposable = receiveUsersObservable.subscribeBy(
                onNext = {
                    println("[ChatMicroservice]: Am primit mesajul: ${it.second}")
                    logger.log(LOG_PATH, "[ChatMicroservice]: Am primit userul: ${it.second}")
                    bidderConnections.add(it.first)
                    bidderInfo.add(it.second)
                },
                onComplete = {
                    println("[ChatMicroservice]: Au fost conectati toti userii la licitatie.")
                    logger.log(LOG_PATH, "[ChatMicroservice]: Au fost conectati toti userii la licitatie.")
                    bidderConnections.forEach {
                        it.getOutputStream().write("Lista Useri:\n".toByteArray())
                        it.getOutputStream().write("${bidderInfo}\n".toByteArray())
                    }
                },
                onError = {
                    println("[AuctioneerMicroservice]: Eroare: ${it.stackTraceToString()}")
                    logger.log(LOG_PATH, "[AuctioneerMicroservice]: Eroare: ${it.stackTraceToString()}")
                }
        )
        subscriptions.add(disposable)
    }

    fun run(){
        saveBidders()
        runChat()

    }
}

fun main(args: Array<String>){
    val chatMicroservice = ChatMicroservice()
    chatMicroservice.run()
}