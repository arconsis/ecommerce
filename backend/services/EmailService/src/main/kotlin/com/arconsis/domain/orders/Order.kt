package com.arconsis.domain.orders

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import com.arconsis.domain.addresses
import java.util.*

data class Order(
    val orderId: UUID,
    val basketId: UUID,
    val userId: UUID,
    val prices: OrderPrices,
    val status: OrderStatus,
    val items: List<OrderItem>,
    // addresses
    val shippingAddress: Address,
    val billingAddress: Address,
    // shipmentProvider
    val shipmentProvider: OrderShipmentProvider
)

enum class OrderStatus {
    REQUESTED,
    VALIDATED,
    OUT_OF_STOCK,
    PAYMENT_IN_PROGRESS,
    PAID,
    PREPARING_SHIPMENT,
    SHIPPED,
    COMPLETED,
    PAYMENT_FAILED,
    CANCELLED,
    REFUNDED,
    CREATING_SHIPMENT_LABEL_FAILED,
    SHIPMENT_DELIVERY_FAILED,
}

data class OrderPrices (
    val totalPrice: BigDecimal,
    val tax: String,
    val priceBeforeTax: BigDecimal,
    val priceBeforeTax: BigDecimal,
    val productPrice: BigDecimal,
    val shippingPrice: BigDecimal,
    val currency: SupportedCurrencies,
)

data class OrderShipmentProvider(
    val name: String,
    val price: BigDecimal,
    val externalShipmentProviderId: String,
    val currency: SupportedCurrencies,
    val carrierAccount: String
)

enum class SupportedCurrencies {
    USD,
}

class OrdersDeserializer : ObjectMapperDeserializer<Order>(Order::class.java)