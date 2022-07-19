package com.arconsis.data.orders

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.data.orderitems.OrderItemEntity
import com.arconsis.data.orders.OrderEntity.Companion.UPDATE_ORDER_STATUS
import com.arconsis.domain.orderitems.OrderItem
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

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

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

    @OneToMany(mappedBy = "orderId", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var itemEntities: MutableList<OrderItemEntity> = mutableListOf()
) {
    companion object {
        const val UPDATE_ORDER_STATUS = "OrderEntity.update_order_status"
    }
}

fun OrderEntity.toOrder() = Order(
    userId = userId,
    orderId = orderId!!,
    amount = amount,
    currency = currency,
    status = status,
    items = itemEntities.map {
        OrderItem(
            itemId = it.itemId!!,
            productId = it.productId,
            orderId = it.orderId,
            price = it.price,
            currency = it.currency,
            quantity = it.quantity
        )
    }
)

fun CreateOrder.toOrderEntity(status: OrderStatus) = OrderEntity(
    userId = userId,
    amount = amount,
    currency = currency,
    status = status,
)