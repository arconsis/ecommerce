package com.arconsis.data.baskets

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.data.addresses.AddressEntity
import com.arconsis.data.addresses.toAddress
import com.arconsis.data.basketitems.BasketItemEntity
import com.arconsis.data.basketitems.toBasketItem
import com.arconsis.data.baskets.BasketEntity.Companion.GET_BY_BASKET_ID
import com.arconsis.data.orders.OrderEntity
import com.arconsis.domain.baskets.Basket
import com.arconsis.domain.baskets.BasketItem
import com.arconsis.domain.baskets.CreateBasket
import org.hibernate.annotations.*
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import javax.persistence.*
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery

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

	@Column(name = "total_price", nullable = false)
	var totalPrice: BigDecimal,

	@Column(name = "price_before_tax", nullable = false)
	var priceBeforeTax: BigDecimal,

	@Column(nullable = false)
	var tax: String,

	@Column(nullable = false)
	var currency: String,

	@CreationTimestamp
	@Column(name = "created_at")
	var createdAt: Instant? = null,

	@UpdateTimestamp
	@Column(name = "updated_at")
	var updatedAt: Instant? = null,

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "basketId", cascade = [CascadeType.ALL])
	var itemEntities: MutableList<BasketItemEntity> = mutableListOf(),

//	@OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
//	@PrimaryKeyJoinColumn
//	var order: OrderEntity? = null,
	@OneToOne(mappedBy = "basket")
	val order: OrderEntity? = null,

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "basketId", cascade = [CascadeType.ALL])
	var addressEntities: MutableList<AddressEntity> = mutableListOf()
) {
	companion object {
		const val GET_BY_BASKET_ID = "BasketEntity.get_by_basket_id"
	}
}

fun BasketEntity.toBasket() = Basket(
	basketId = basketId!!,
	userId = userId,
	totalPrice = totalPrice,
	tax = tax,
	priceBeforeTax = priceBeforeTax,
	currency = currency,
	items = itemEntities.map { it.toBasketItem() },
	shippingAddress = addressEntities.find { it.isSelected }?.toAddress(),
	billingAddress = addressEntities.find { it.isBilling }?.toAddress(),
	isOrderable = addressEntities.find { it.isSelected } != null && addressEntities.find { it.isBilling } != null
)

fun CreateBasket.toBasketEntity() = BasketEntity(
	userId = userId,
	totalPrice = totalPrice,
	priceBeforeTax = priceBeforeTax,
	tax = tax,
	currency = currency
)

