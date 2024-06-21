package com.sd.laborator

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaStreamingContext
import java.util.*

fun printData(jsonObj: List<String>){
    if(jsonObj.size != 0){
        val jsonData = Json.parseToJsonElement(jsonObj[0])
        val news = jsonData.jsonArray[0].jsonObject
        if("Yahoo" !in news.getValue("source").toString() &&  news.getValue("summary").toString().length <= 500 ) {
            val timestamp = news.getValue("datetime").toString().toLong()
            val date = Date(timestamp)

            println()

            println("=======================================")
            println(news.getValue("headline").toString())
            println(date)
            println(news.getValue("url").toString().replace("\"", ""))
            println("=======================================")

            println()

        }
    }
}


fun main() {
    val conf = SparkConf().setMaster("local[2]").setAppName("News")
    val jssc = JavaStreamingContext(conf, Durations.seconds(1))

    val stream = jssc.socketTextStream("127.0.0.1", 65432)
    stream.foreachRDD { rdd-> printData(rdd.collect()) }

    jssc.start()
    jssc.awaitTermination()
}