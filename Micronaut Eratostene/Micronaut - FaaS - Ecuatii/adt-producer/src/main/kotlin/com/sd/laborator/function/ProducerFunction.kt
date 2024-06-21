package com.sd.laborator.function

import com.sd.laborator.model.AdtData
import com.sd.laborator.producer.MessageProducer
import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer
import kotlin.random.Random

@FunctionBean("adtGenerator")
class ProducerFunction: FunctionInitializer(), Consumer<AdtData> {
    @Inject
    private lateinit var messageProducer: MessageProducer

    private val logger: Logger = LoggerFactory.getLogger(ProducerFunction::class.java)
    private val listLength: Int = 100

    override fun accept(adtData: AdtData) {
        logger.info("Values: ${adtData.aLength}, ${adtData.bLength}")

        val random = Random(7837)

        val stringBuilder = StringBuilder()
        stringBuilder.append(
            when(adtData.aLength) {
                0 -> List(listLength) { random.nextInt(0, listLength * 10) }
                else -> List(adtData.aLength) { random.nextInt(0, adtData.aLength * 10) }
            }
        )
        stringBuilder.append(";")

        stringBuilder.append(
            when(adtData.bLength) {
                0 -> List(listLength) { random.nextInt(0, listLength * 10) }
                else -> List(adtData.bLength) { random.nextInt(0, adtData.bLength * 10) }
            }
        )
        logger.info("Send data: $stringBuilder")
        messageProducer.send(stringBuilder.toString())
    }
}