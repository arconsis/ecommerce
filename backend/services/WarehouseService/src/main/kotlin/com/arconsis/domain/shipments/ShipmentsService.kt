package com.arconsis.domain.shipments

import com.arconsis.data.common.asPair
import com.arconsis.data.common.toUni
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.shipments.ShipmentsRepository
import com.arconsis.domain.shipmentproviders.ShipmentProvider
import com.arconsis.presentation.http.shipments.dto.DeliveryAddressDto
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsService(
    private val shipmentsRepository: ShipmentsRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val objectMapper: ObjectMapper,
) {
    fun updateShipment(updateShipment: UpdateShipment): Uni<Shipment> {
        return sessionFactory.withTransaction { session, _ ->
            shipmentsRepository.updateShipmentStatus(updateShipment.shipmentId, updateShipment.status, session)
                .flatMap { shipment ->
                    val createOutboxEvent = shipment.toCreateOutboxEvent(objectMapper)
                    Uni.combine().all().unis(
                        outboxEventsRepository.createEvent(createOutboxEvent, session),
                        shipment.toUni(),
                    ).asPair()
                }
                .map { (_, shipment) ->
                    shipment
                }
        }
    }

    suspend fun listShipmentProviders(deliveryAddress: DeliveryAddressDto): List<ShipmentProvider> {
        return shipmentsRepository.listShipmentProviders(deliveryAddress)
    }
}