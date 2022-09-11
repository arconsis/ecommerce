package com.arconsis.data.orders

import com.arconsis.common.TAX_RATE
import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.data.shippingaddresses.toAddress
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

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, columnDefinition = "supported_currencies")
    @Type(type = "pgsql_enum")
    var currency: SupportedCurrencies,

    @Column(name = "shipping_provider_name", nullable = false)
    var shippingProviderName: String,

    @Column(name = "external_shipping_provider_id", nullable = false)
    var externalShippingProviderId: String,

    @Column(name = "carrier_account", nullable = false)
    var carrierAccount: String,

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
    pricing = OrderPrices(
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
    paymentMethod = OrderPaymentMethod(pspToken = pspToken, paymentMethodType = paymentMethodType),
    shippingAddress = basket.shippingAddressEntities.find { it.isSelected }!!.toAddress(),
    billingAddress = basket.shippingAddressEntities.find { it.isBilling }!!.toAddress(),
    shippingProvider = OrderShippingProvider(
        name = shippingProviderName,
        externalShippingProviderId = externalShippingProviderId,
        price = shippingPrice,
        currency = currency,
        carrierAccount = carrierAccount
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
    externalShippingProviderId = basket.externalShippingProviderId!!,
    shippingProviderName = basket.shippingProviderName!!,
    carrierAccount = basket.carrierAccount!!
)