package com.arconsis.data.baskets

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.data.addresses.AddressEntity
import com.arconsis.data.addresses.toAddress
import com.arconsis.data.basketitems.BasketItemEntity
import com.arconsis.data.basketitems.toBasketItem
import com.arconsis.data.baskets.BasketEntity.Companion.GET_BY_BASKET_ID
import com.arconsis.data.baskets.BasketEntity.Companion.UPDATE_BASKET_PAYMENT_METHOD
import com.arconsis.data.orders.OrderEntity
import com.arconsis.domain.baskets.Basket
import com.arconsis.domain.baskets.CreateBasket
import com.arconsis.domain.orders.OrderPaymentMethod
import com.arconsis.domain.orders.OrderPaymentMethodType
import com.arconsis.domain.orders.OrderPrices
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
	),
	NamedQuery(
		name = UPDATE_BASKET_PAYMENT_METHOD,
		query = """
            update baskets b
            set b.pspToken = :pspToken, b.paymentMethodType = :paymentMethodType
            where b.basketId = :basketId
        """
	),
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

	@Column(name = "psp_token", nullable = true, unique = true)
	var pspToken: String? = null,

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method_type", nullable = true)
	@Type(type = "pgsql_enum")
	var paymentMethodType: OrderPaymentMethodType? = null,

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
		const val UPDATE_BASKET_PAYMENT_METHOD = "BasketEntity.update_basket_payment_method"
	}
}

fun BasketEntity.toBasket() = Basket(
	basketId = basketId!!,
	userId = userId,
	prices = OrderPrices(
		totalPrice = totalPrice,
		tax = tax,
		priceBeforeTax = priceBeforeTax,
		currency = currency,
	),
	items = itemEntities.map { it.toBasketItem() },
	paymentMethod = if (paymentMethodType != null && pspToken != null) {
		OrderPaymentMethod(paymentMethodType = paymentMethodType!!, pspToken = pspToken!!)
	} else null,
	shippingAddress = addressEntities.find { it.isSelected }?.toAddress(),
	billingAddress = addressEntities.find { it.isBilling }?.toAddress(),
	isOrderable = isBasketOrderable(),
)

fun BasketEntity.isBasketOrderable(): Boolean = addressEntities.find { it.isSelected } != null
		&& addressEntities.find { it.isBilling } != null
		&& pspToken != null
		&& paymentMethodType != null

fun CreateBasket.toBasketEntity() = BasketEntity(
	userId = userId,
	totalPrice = totalPrice,
	priceBeforeTax = priceBeforeTax,
	tax = tax,
	currency = currency
)

