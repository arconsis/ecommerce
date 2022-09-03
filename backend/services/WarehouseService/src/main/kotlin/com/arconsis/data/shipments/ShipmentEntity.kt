package com.arconsis.data.shipments

import com.arconsis.data.common.PostgreSQLEnumType
import com.arconsis.domain.orders.SupportedCurrencies
import com.arconsis.domain.shipments.CreateShipment
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentStatus
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import javax.persistence.*

@NamedQueries(
    NamedQuery(
        name = ShipmentEntity.GET_BY_SHIPMENT_ID,
        query = """
            select s from shipments s
			where s.shipmentId = :shipmentId
        """
    ),
    NamedQuery(
        name = ShipmentEntity.UPDATE_STATUS,
        query = """
            update shipments s
			set s.status = :status
			where s.shipmentId = :shipmentId
        """
    )
)
@Entity(name = "shipments")
@TypeDef(
    name = "pgsql_enum",
    typeClass = PostgreSQLEnumType::class
)
class ShipmentEntity(
    @Id
    @GeneratedValue
    @Column(name = "shipment_id")
    var shipmentId: UUID? = null,

    @Column(name = "external_shipment_id", nullable = true)
    var externalShipmentId: String?,

    @Column(name = "shipment_failure_reason", nullable = true)
    var shipmentFailureReason: String?,

    @Column(name = "external_shipping_provider_id", nullable = false)
    var externalShippingProviderId: String,

    @Column(name = "shipping_provider_name", nullable = false)
    var providerName: String,

    @Column(name = "external_shipment_label_id", nullable = true, unique = true)
    var externalShipmentLabelId: String?,

    @Column(name = "price", nullable = false)
    var price: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, columnDefinition = "supported_currencies")
    @Type(type = "pgsql_enum")
    var currency: SupportedCurrencies,

    @Column(name = "order_id", nullable = false)
    var orderId: UUID,

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "shipment_status")
    @Type(type = "pgsql_enum")
    var status: ShipmentStatus,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
) {
    companion object {
        const val GET_BY_SHIPMENT_ID = "ShipmentEntity.get_by_shipment_id"
        const val UPDATE_STATUS = "ShipmentEntity.update_status"
    }
}

fun ShipmentEntity.toShipment() = Shipment(
    shipmentId = shipmentId!!,
    orderId = orderId,
    status = status,
    userId = userId,
    externalShipmentId = externalShipmentId,
    shipmentFailureReason = shipmentFailureReason,
    externalShippingProviderId = externalShippingProviderId,
    externalShipmentLabelId = externalShipmentLabelId,
    price = price,
    currency = currency,
    providerName = providerName
)

fun CreateShipment.toShipmentEntity(status: ShipmentStatus) = ShipmentEntity(
    orderId = orderId,
    externalShipmentId = externalShipmentId,
    externalShippingProviderId = externalShippingProviderId,
    externalShipmentLabelId = externalShipmentLabelId,
    price = price,
    currency = currency,
    providerName = providerName,
    userId = userId,
    status = status,
    shipmentFailureReason = shipmentFailureReason
)