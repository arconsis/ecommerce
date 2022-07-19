package com.arconsis.domain.shipments

import com.arconsis.data.common.asPair
import com.arconsis.data.common.toUni
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.shipments.ShipmentsRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsService(
    private val shipmentsRepository: ShipmentsRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
) {
    fun updateShipment(updateShipment: UpdateShipment): Uni<Shipment> {
        return shipmentsRepository.updateShipmentStatus(updateShipment.shipmentId, updateShipment.status)
            .flatMap { shipment ->
                val createOutboxEvent = shipment.toCreateOutboxEvent(objectMapper)
                Uni.combine().all().unis(
                    outboxEventsRepository.createEvent(createOutboxEvent),
                    shipment.toUni(),
                ).asPair()
            }
            .map { (_, shipment) ->
                shipment
            }
    }
}