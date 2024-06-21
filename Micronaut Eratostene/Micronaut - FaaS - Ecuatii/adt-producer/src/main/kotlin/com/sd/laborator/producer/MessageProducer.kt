package com.sd.laborator.producer

import io.micronaut.core.annotation.Introspected
import io.micronaut.rabbitmq.annotation.Binding
import io.micronaut.rabbitmq.annotation.RabbitClient

@Introspected
@RabbitClient("micronaut.adt.direct")
interface MessageProducer {
    @Binding("micronaut.adt.routingkey")
    fun send(data: String)
}