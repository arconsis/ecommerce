package com.arconsis.data.basketitems

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.domain.baskets.BasketItem
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity(name = "basket_items")
@TypeDef(
	name = "pgsql_enum",
	typeClass = PostgreSQLEnumType::class
)
class BasketItemEntity(
	@Id
	@GeneratedValue
	@Column(name = "item_id")
	var itemId: UUID? = null,

	// FK
	@Column(name = "basket_id")
	var basketId: UUID,

	@Column(name = "product_id")
	var productId: UUID,

	@Column(nullable = false)
	var price: BigDecimal,

	@Column(nullable = false)
	var currency: String,

	@Column(nullable = false)
	var quantity: Int,

	@Column(nullable = false)
	var thumbnail: String,

	@Column(name = "product_name", nullable = false)
	var productName: String,

	@Column(nullable = false)
	var description: String,

	@CreationTimestamp
	@Column(name = "created_at")
	var createdAt: Instant? = null,

	@UpdateTimestamp
	@Column(name = "updated_at")
	var updatedAt: Instant? = null,
)

fun BasketItemEntity.toBasketItem() = BasketItem(
	itemId = itemId!!,
	basketId = basketId,
	productId = productId,
	thumbnail =thumbnail,
	productName = productName,
	description = description,
	currency = currency,
	price = price,
	quantity = quantity
)


