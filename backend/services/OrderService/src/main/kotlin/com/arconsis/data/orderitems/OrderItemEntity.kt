package com.arconsis.data.orderitems

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.domain.orderitems.OrderItem
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import javax.persistence.*

@NamedQueries(
	NamedQuery(
		name = OrderItemEntity.GET_BY_ITEM_ID,
		query = """
            select p from order_items p
			where p.itemId = :itemId
        """
	)
)
@Entity(name = "order_items")
@TypeDef(
	name = "pgsql_enum",
	typeClass = PostgreSQLEnumType::class
)
class OrderItemEntity(
	@Id
	@GeneratedValue
	@Column(name = "item_id")
	var itemId: UUID? = null,

	@Column(name = "product_id")
	var productId: UUID,

	@Column(name = "order_id")
	var orderId: UUID,

	@Column(nullable = false)
	var price: BigDecimal,

	@Column(nullable = false)
	var currency: String,

	@Column(nullable = false)
	var quantity: Int,

	@CreationTimestamp
	@Column(name = "created_at")
	var createdAt: Instant? = null,

	@UpdateTimestamp
	@Column(name = "updated_at")
	var updatedAt: Instant? = null,
) {
	companion object {
		const val GET_BY_ITEM_ID = "OrderProductEntity.get_by_item_id"
	}
}

fun OrderItemEntity.toProduct() = OrderItem(
	itemId = itemId!!,
	productId = productId,
	orderId = orderId,
	price = price,
	currency = currency,
	quantity = quantity
)

