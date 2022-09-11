package com.arconsis.data.baskets

import com.arconsis.common.TAX_RATE
import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.data.shippingaddresses.ShippingAddressEntity
import com.arconsis.data.shippingaddresses.toAddress
import com.arconsis.data.basketitems.BasketItemEntity
import com.arconsis.data.basketitems.toBasketItem
import com.arconsis.data.baskets.BasketEntity.Companion.GET_BY_BASKET_ID
import com.arconsis.data.baskets.BasketEntity.Companion.UPDATE_BASKET_PAYMENT_METHOD
import com.arconsis.data.baskets.BasketEntity.Companion.UPDATE_BASKET_SHIPPING_PROVIDER
import com.arconsis.data.orders.OrderEntity
import com.arconsis.domain.baskets.Basket
import com.arconsis.domain.baskets.CreateBasket
import com.arconsis.domain.orders.*
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
	NamedQuery(
		name = UPDATE_BASKET_SHIPPING_PROVIDER,
		query = """
            update baskets b
            set b.shippingProviderName = :shippingProviderName, b.externalShippingProviderId = :externalShippingProviderId, b.shippingPrice = :shippingPrice, b.carrierAccount = :carrierAccount, b.totalPrice = :shippingPrice + b.totalPrice
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

	@Column(name = "shipping_price", nullable = false)
	var shippingPrice: BigDecimal,

	@Column(name = "product_price", nullable = false)
	var productPrice: BigDecimal,

	@Column(name = "shipping_provider_name", nullable = true)
	var shippingProviderName: String?,

	@Column(name = "external_shipping_provider_id", nullable = true)
	var externalShippingProviderId: String?,

	@Column(name = "carrier_account", nullable = true)
	var carrierAccount: String?,

	@Column(nullable = false)
	var tax: String,

	@Enumerated(EnumType.STRING)
	@Column(name = "currency", nullable = false, columnDefinition = "supported_currencies")
	@Type(type = "pgsql_enum")
	var currency: SupportedCurrencies,

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

	@OneToOne(mappedBy = "basket")
	val order: OrderEntity? = null,

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "basketId", cascade = [CascadeType.ALL])
	var shippingAddressEntities: MutableList<ShippingAddressEntity> = mutableListOf()
) {
	companion object {
		const val GET_BY_BASKET_ID = "BasketEntity.get_by_basket_id"
		const val UPDATE_BASKET_PAYMENT_METHOD = "BasketEntity.update_basket_payment_method"
		const val UPDATE_BASKET_SHIPPING_PROVIDER = "BasketEntity.update_basket_shipping_provider"
	}
}

fun BasketEntity.toBasket() = Basket(
	basketId = basketId!!,
	userId = userId,
	pricing = OrderPrices(
		totalPrice = totalPrice,
		tax = tax,
		priceBeforeTax = priceBeforeTax,
		priceAfterTax = priceBeforeTax.multiply(BigDecimal(1) + BigDecimal(TAX_RATE)).setScale(2),
		productPrice = productPrice,
		shippingPrice = shippingPrice,
		currency = currency,
	),
	items = itemEntities.map { it.toBasketItem() },
	paymentMethod = if (paymentMethodType != null && pspToken != null) {
		OrderPaymentMethod(paymentMethodType = paymentMethodType!!, pspToken = pspToken!!)
	} else null,
	shippingShippingAddress = shippingAddressEntities.find { it.isSelected }?.toAddress(),
	billingShippingAddress = shippingAddressEntities.find { it.isBilling }?.toAddress(),
	isOrderable = isBasketOrderable(),
	shippingProvider = if (shippingProviderName != null && externalShippingProviderId != null && carrierAccount != null) {
		OrderShippingProvider(
			shippingProviderName!!,
			shippingPrice,
			externalShippingProviderId!!,
			currency,
			carrierAccount!!
		)
	} else null
)

fun BasketEntity.isBasketOrderable(): Boolean = shippingAddressEntities.find { it.isSelected } != null
		&& shippingAddressEntities.find { it.isBilling } != null
		&& pspToken != null
		&& paymentMethodType != null
		&& externalShippingProviderId != null
		&& carrierAccount != null

fun CreateBasket.toBasketEntity() = BasketEntity(
	userId = userId,
	totalPrice = totalPrice,
	priceBeforeTax = priceBeforeTax,
	tax = tax,
	currency = currency,
	productPrice = productPrice,
	shippingPrice = shippingPrice,
	externalShippingProviderId = null,
	shippingProviderName = null,
	carrierAccount = null
)

