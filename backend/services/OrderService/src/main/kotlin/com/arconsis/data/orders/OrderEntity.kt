package com.arconsis.data.orders

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.data.orders.OrderEntity.Companion.UPDATE_ORDER_STATUS
import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
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
    @Column(name = "basket_id", unique = true)
    var basketId: UUID,

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    // TODO: perhaps can be moved to another table 1:1
    // checkout session from PSP
    @Column(name = "checkout_session_id", nullable = true, unique = true)
    var checkoutSessionId: String? = null,

    // checkout session from PSP
    @Column(name = "checkout_url", nullable = true, unique = true)
    var checkoutUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "order_status")
    @Type(type = "pgsql_enum")
    var status: OrderStatus,

    @Column(nullable = false)
    var amount: BigDecimal,

    @Column(nullable = false)
    var currency: String,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
) {
    companion object {
        const val UPDATE_ORDER_STATUS = "OrderEntity.update_order_status"
    }
}

fun OrderEntity.toOrder() = Order(
    orderId = orderId!!,
    basketId = basketId,
    userId = userId,
    amount = amount,
    currency = currency,
    status = status,
    items = emptyList(),
    checkoutSessionId = checkoutSessionId,
    checkoutUrl = checkoutUrl
)

fun CreateOrder.toOrderEntity(status: OrderStatus) = OrderEntity(
    basketId = basketId,
    userId = userId,
    amount = amount,
    currency = currency,
    status = status,
)