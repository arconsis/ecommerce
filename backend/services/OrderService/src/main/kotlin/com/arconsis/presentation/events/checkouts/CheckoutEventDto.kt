package com.arconsis.presentation.events.checkouts

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.OutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class CheckoutEventDto(
    @JsonProperty("payload") val payload: CheckoutEventDtoPayload
)

data class CheckoutEventDtoPayload(
    @JsonProperty("before") val previousValue: CheckoutEventDtoValue? = null,
    @JsonProperty("after") val currentValue: CheckoutEventDtoValue,
)

data class CheckoutEventDtoValue(
    @JsonProperty("id") var id: String,
    @JsonProperty("aggregate_type") var aggregateType: String,
    @JsonProperty("aggregate_id") var aggregateId: String,
    @JsonProperty("payload") val payload: String,
    @JsonProperty("type") val type: String,
)

fun CheckoutEventDtoValue.toOutboxEvent() = OutboxEvent(
    id = UUID.fromString(id),
    aggregateId = UUID.fromString(aggregateId),
    aggregateType = AggregateType.valueOf(aggregateType),
    type = OutboxEventType.valueOf(type),
    payload = payload
)

class CheckoutEventsDtoDeserializer : ObjectMapperDeserializer<CheckoutEventDto>(CheckoutEventDto::class.java)
