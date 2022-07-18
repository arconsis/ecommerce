package com.arconsis.data.products

import com.arconsis.data.common.PostgreSQLEnumType
import com.arconsis.domain.products.CreateProduct
import com.arconsis.domain.products.Product
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity(name = "products")
@TypeDef(
	name = "pgsql_enum",
	typeClass = PostgreSQLEnumType::class
)
class ProductEntity(
	@Id
	@GeneratedValue
	@Column(name = "product_id")
	var productId: UUID? = null,

	var thumbnail: String,
	var productName: String,
	var description: String,
	var price: BigDecimal,
	var currency: String,

	@CreationTimestamp
	@Column(name = "created_at")
	var createdAt: Instant? = null,

	@UpdateTimestamp
	@Column(name = "updated_at")
	var updatedAt: Instant? = null,
)

fun ProductEntity.toProduct() = Product(
	productId = productId!!,
	thumbnail = thumbnail,
	productName = productName,
	description = description,
	price = price,
	isOrderable = null,
	currency = currency
)

fun CreateProduct.toProductEntity() = ProductEntity(
	thumbnail = thumbnail,
	productName = productName,
	description = description,
	price = price,
	currency = currency
)