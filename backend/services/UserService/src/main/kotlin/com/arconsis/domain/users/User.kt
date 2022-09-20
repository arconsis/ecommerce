package com.arconsis.domain.users

import com.arconsis.domain.addresses.Address
import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

data class User(
    val userId: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
    val addresses: List<Address>,
    val sub: String
)

fun User.toCreateOutboxEvent(objectMapper: ObjectMapper): CreateOutboxEvent = CreateOutboxEvent(
    aggregateType = AggregateType.USER,
    aggregateId = this.userId,
    type = OutboxEventType.USER_CREATED.name,
    payload = objectMapper.writeValueAsString(this)
)