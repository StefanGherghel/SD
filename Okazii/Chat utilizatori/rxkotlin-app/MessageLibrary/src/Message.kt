import java.text.SimpleDateFormat
import java.util.*

class Message private constructor(
    val sender: String,
    val body: String,
    val timestamp: Date,
    val name: String="bob",
    val telephone: String = "-",
    val email: String = "-"
) {
    companion object{
        fun create(sender: String, body: String, name: String = "-", telephone: String="-", email: String="-"): Message{
            return Message(sender, body, Date(), name, telephone, email)
        }

        fun deserialize(msg: ByteArray): Message{
            val msgString = String(msg)
            val (timestamp, sender, body, name, telephone, email) = msgString.split(" ", limit = 6)

            return Message(
                    sender,
                    body,
                    Date(timestamp.toLong()),
                    name,
                    telephone,
                    email
            )
        }
    }

    fun serialize(): ByteArray = "${timestamp.time} $sender $body $name $telephone $email\n".toByteArray()

    override fun toString(): String {
        val dateString =  SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(timestamp)
        return "[$dateString] $sender {Name: $name, Telephone: $telephone, Email: $email} >>> $body"
    }
}

private operator fun <E> List<E>.component6(): E = get(5)


fun main(args: Array<String>){
    val msg = Message.create("localhost:4848", "test mesaj")
    println(msg)

    val serialized = msg.serialize()
    val deserialize = Message.deserialize(serialized)
    println(deserialize)
}