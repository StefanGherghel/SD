package org.example

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaStreamingContext


fun main() {
    val streamIP = "localhost"
    val streamSocket = 1502
    val sparkConfig = SparkConf()
        .setMaster("local[*]")
        .setAppName("kotlin_streaming")
        .set("spark.executor.cores", "*")
    val streamingContext = JavaStreamingContext(sparkConfig, Durations.seconds(3))

    val data = streamingContext.socketTextStream(streamIP, streamSocket)
    val dataJSON = data
            .map {
                Json { ignoreUnknownKeys = true}.decodeFromString<Data>(it)
            }.filter{
                it.exchange !=  "NEW YORK STOCK EXCHANGE, INC." && it.ipo != "" && it.phone != ""
            }.filter {
                2020 - it.ipo.split("-")[0].toInt() > 5
            }

    dataJSON.foreachRDD {
        dataRDD ->
            dataRDD.foreach {
                println("Nume:${it.name} \t Telefon:${it.phone} \t Piata de capital:${it.marketCapitalization}")
            }
    }

    streamingContext.start()
    streamingContext.awaitTermination()
    streamingContext.stop()
}