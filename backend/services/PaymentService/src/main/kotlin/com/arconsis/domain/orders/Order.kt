package com.arconsis.domain.orders

import com.arconsis.domain.shippingaddresses.ShippingAddress
import com.arconsis.domain.checkouts.Checkout
import com.arconsis.domain.checkouts.CheckoutStatus
import com.arconsis.domain.checkouts.CreateCheckout
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.math.BigDecimal
import java.util.*

data class Order(
    val orderId: UUID,
    val basketId: UUID,
    val userId: UUID,
    val prices: OrderPrices,
    val status: OrderStatus,
    val items: List<OrderItem>,
    // psp ref
    val paymentMethod: OrderPaymentMethod,
    val checkout: Checkout?,
    // addresses
    val shippingAddress: ShippingAddress,
    val billingAddress: ShippingAddress,
    // shippingProvider
    val shippingProvider: OrderShippingProvider
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
    val priceBeforeTax: BigDecimal,
    val priceAfterTax: BigDecimal,
    val productPrice: BigDecimal,
    val shippingPrice: BigDecimal,
    val currency: SupportedCurrencies,
)

enum class OrderPaymentMethodType {
    STRIPE,
    CASH_ON_DELIVERY
}

data class OrderShippingProvider(
    val name: String,
    val price: BigDecimal,
    val externalShippingProviderId: String,
    val currency: SupportedCurrencies,
    val carrierAccount: String
)

enum class SupportedCurrencies {
    USD,
}

fun Order.toCreateCheckout(pspToken: String, paymentStatus: CheckoutStatus) = CreateCheckout(
    userId = userId,
    orderId = orderId,
    amount = prices.totalPrice,
    currency = prices.currency,
    paymentStatus = paymentStatus,
    paymentErrorMessage = null,
    paymentErrorCode = null,
    pspToken = pspToken,
)

class OrdersDeserializer : ObjectMapperDeserializer<Order>(Order::class.java)