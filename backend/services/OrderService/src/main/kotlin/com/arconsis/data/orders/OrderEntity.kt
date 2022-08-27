package com.arconsis.data.orders

import com.arconsis.common.TAX_RATE
import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.data.addresses.toAddress
import com.arconsis.data.basketitems.toBasketItem
import com.arconsis.data.baskets.BasketEntity
import com.arconsis.data.orders.OrderEntity.Companion.UPDATE_ORDER_STATUS
import com.arconsis.domain.baskets.toOrderItem
import com.arconsis.domain.orders.*
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
        name = UPDATE_ORDER_STATUS,
        query = """
            update orders o
            set o.status = :status
            where o.orderId = :orderId
        """
    ),
)
@Entity(name = "orders")
@TypeDef(
    name = "pgsql_enum",
    typeClass = PostgreSQLEnumType::class
)
class OrderEntity(
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    var orderId: UUID? = null,

    // FK
    @Column(name = "basket_id", unique = true, insertable = false, updatable = false)
    var basketId: UUID,

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "order_status")
    @Type(type = "pgsql_enum")
    var status: OrderStatus,

    @Column(name = "total_price", nullable = false)
    var totalPrice: BigDecimal,

    @Column(name = "price_before_tax", nullable = false)
    var priceBeforeTax: BigDecimal,

    @Column(name = "shipping_price", nullable = false)
    var shippingPrice: BigDecimal,

    @Column(name = "product_price", nullable = false)
    var productPrice: BigDecimal,

    @Column(nullable = false)
    var tax: String,

    @Column(nullable = false)
    var currency: String,

    @Column(name = "shipment_provider_name", nullable = false)
    var shipmentProviderName: String,

    @Column(name = "external_shipment_provider_id", nullable = false)
    var externalShipmentProviderId: String,

    @Column(name = "psp_token", nullable = false, unique = true)
    var pspToken: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method_type", nullable = false)
    @Type(type = "pgsql_enum")
    var paymentMethodType: OrderPaymentMethodType,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "basket_id", referencedColumnName = "basket_id")
    val basket: BasketEntity
) {
    companion object {
        const val UPDATE_ORDER_STATUS = "OrderEntity.update_order_status"
    }
}

fun OrderEntity.toOrder() = Order(
    orderId = orderId!!,
    basketId = basketId,
    userId = userId,
    prices = OrderPrices(
        totalPrice = totalPrice,
        tax = tax,
        priceBeforeTax = priceBeforeTax,
        priceAfterTax = priceBeforeTax.multiply(BigDecimal(1) + BigDecimal(TAX_RATE)).setScale(2),
        currency = currency,
        shippingPrice = shippingPrice,
        productPrice = productPrice
    ),
    status = status,
    items = basket.itemEntities.map { it.toBasketItem().toOrderItem(orderId!!) },
//    checkoutSessionId = checkoutSessionId,
//    checkoutUrl = checkoutUrl,
    paymentMethod = OrderPaymentMethod(pspToken = pspToken, paymentMethodType = paymentMethodType),
    shippingAddress = basket.addressEntities.find { it.isSelected }?.toAddress(),
    billingAddress = basket.addressEntities.find { it.isBilling }?.toAddress(),
    shipmentProvider = OrderShipmentProvider(
        name = shipmentProviderName,
        externalShipmentProviderId = externalShipmentProviderId,
        price = shippingPrice
    )
)

fun CreateOrder.toOrderEntity(status: OrderStatus, basket: BasketEntity) = OrderEntity(
    basketId = basketId,
    userId = userId,
    totalPrice = totalPrice,
    priceBeforeTax = priceBeforeTax,
    productPrice = priceBeforeTax,
    shippingPrice = basket.shippingPrice,
    tax = tax,
    currency = currency,
    status = status,
    basket = basket,
    paymentMethodType = paymentMethodType,
    pspToken = pspToken,
    externalShipmentProviderId = basket.externalShipmentProviderId!!,
    shipmentProviderName = basket.shipmentProviderName!!
)