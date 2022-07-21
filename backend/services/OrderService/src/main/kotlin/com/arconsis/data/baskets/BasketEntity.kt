package com.arconsis.data.baskets

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.data.basketitems.BasketItemEntity
import com.arconsis.data.baskets.BasketEntity.Companion.GET_BY_BASKET_ID
import com.arconsis.data.orders.OrderEntity
import com.arconsis.domain.baskets.Basket
import com.arconsis.domain.baskets.BasketItem
import com.arconsis.domain.baskets.CreateBasket
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import javax.persistence.*

@NamedQueries(
	NamedQuery(
		name = GET_BY_BASKET_ID,
		query = """
            select p from baskets p
			where p.basketId = :basketId
        """
	)
)
@Entity(name = "baskets")
@TypeDef(
	name = "pgsql_enum",
	typeClass = PostgreSQLEnumType::class
)
class BasketEntity(
	@Id
	@GeneratedValue
	@Column(name = "basket_id")
	var basketId: UUID? = null,

	@Column(name = "user_id")
	var userId: UUID,

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

	@OneToMany(mappedBy = "basketId", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
	var itemEntities: MutableList<BasketItemEntity> = mutableListOf(),

	@OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	var order: OrderEntity? = null
) {
	companion object {
		const val GET_BY_BASKET_ID = "BasketEntity.get_by_basket_id"
	}
}

fun BasketEntity.toBasket() = Basket(
	basketId = basketId!!,
	userId = userId,
	amount = amount,
	currency = currency,
	items = itemEntities.map {
		BasketItem(
			itemId = it.itemId!!,
			basketId = it.basketId,
			productId = it.productId,
			thumbnail = it.thumbnail,
			productName = it.productName,
			description = it.description,
			currency = it.currency,
			price = it.price,
			quantity = it.quantity
		)
	},
)

fun CreateBasket.toBasketEntity() = BasketEntity(
	userId = userId,
	amount = amount,
	currency = currency
)

