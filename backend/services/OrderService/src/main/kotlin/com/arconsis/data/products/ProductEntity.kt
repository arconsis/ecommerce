package com.arconsis.data.products

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.domain.orders.SupportedCurrencies
import com.arconsis.domain.products.*
import org.hibernate.annotations.*
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery

@NamedQueries(
	NamedQuery(
		name = ProductEntity.GET_BY_PRODUCT_ID,
		query = """
            select p from products p
			where p.productId = :productId
        """
	)
)
@Entity(name = "products")
@TypeDef(
	name = "pgsql_enum",
	typeClass = PostgreSQLEnumType::class
)
class ProductEntity(
	@Id
	@Column(name = "product_id")
	var productId: UUID,
	var name: String,
	val slug: String,
	val sku: String,
	var description: String,
	var price: BigDecimal,
	var thumbnail: String,

	@Enumerated(EnumType.STRING)
	@Column(name = "currency", nullable = false, columnDefinition = "supported_currencies")
	@Type(type = "pgsql_enum")
	var currency: SupportedCurrencies,

	@CreationTimestamp
	@Column(name = "created_at")
	var createdAt: Instant? = null,

	@UpdateTimestamp
	@Column(name = "updated_at")
	var updatedAt: Instant? = null,
) {
	companion object {
		const val GET_BY_PRODUCT_ID = "ProductEntity.get_by_product_id"
	}
}

fun ProductEntity.toProduct() = Product(
	productId = productId,
	name = name,
	slug = slug,
	sku = sku,
	description = description,
	price = price,
	currency = currency,
	thumbnail = thumbnail,
)

fun Product.toProductEntity() = ProductEntity(
	productId = productId,
	name = name,
	slug = slug,
	sku = sku,
	description = description,
	price = price,
	currency = currency,
	thumbnail = thumbnail,
)