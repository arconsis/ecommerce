package com.arconsis.domain.orders

import com.arconsis.domain.shippingaddresses.ShippingAddress
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.math.BigDecimal
import java.util.*

data class Order(
	val orderId: UUID,
	val basketId: UUID,
	val userId: UUID,
	val pricing: OrderPrices,
	val status: OrderStatus,
	val items: List<OrderItem>,
    // psp ref
	val paymentMethod: OrderPaymentMethod,
    // addresses
	val shippingAddress: ShippingAddress,
	val billingAddress: ShippingAddress,
    // shipping
	val shippingProvider: OrderShippingProvider
)

data class Product(
    val productId: UUID,
    val price: BigDecimal,
    val currency: SupportedCurrencies
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

data class OrderPaymentMethod (
    val pspToken: String,
    val paymentMethodType: OrderPaymentMethodType
)

data class OrderPrices (
    val totalPrice: BigDecimal,
    val tax: String,
    val priceAfterTax: BigDecimal,
    val priceBeforeTax: BigDecimal,
    val productPrice: BigDecimal,
    val shippingPrice: BigDecimal,
    val currency: SupportedCurrencies,
)

enum class OrderPaymentMethodType {
    STRIPE,
    CASH_ON_DELIVERY
}

enum class SupportedCurrencies {
	USD,
}

data class OrderShippingProvider(
    val name: String,
    val price: BigDecimal,
    val externalShippingProviderId: String,
	val currency: SupportedCurrencies,
	val carrierAccount: String
)

class OrdersDeserializer : ObjectMapperDeserializer<Order>(Order::class.java)