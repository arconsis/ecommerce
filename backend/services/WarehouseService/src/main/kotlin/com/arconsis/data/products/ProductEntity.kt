package com.arconsis.data.products

import com.arconsis.data.common.PostgreSQLEnumType
import com.arconsis.data.inventory.InventoryEntity
import com.arconsis.data.productmedia.ProductMediaEntity
import com.arconsis.domain.orders.SupportedCurrencies
import com.arconsis.domain.productmedia.ProductMedia
import com.arconsis.domain.products.*
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
	@GeneratedValue
	@Column(name = "product_id")
	var productId: UUID? = null,
	var name: String,
	val slug: String,
	val sku: String,
	var description: String,
	var price: BigDecimal,

	@Enumerated(EnumType.STRING)
	@Column(name = "currency", nullable = false, columnDefinition = "supported_currencies")
	@Type(type = "pgsql_enum")
	var currency: SupportedCurrencies,

	var tags: String,
	val height: Long?,
	val width: Long?,
	val length: Long?,

	@Enumerated(EnumType.STRING)
	@Column(name = "width_unit", nullable = true, columnDefinition = "product_width_enum")
	@Type(type = "pgsql_enum")
	val widthUnit: ProductSizeUnit?,

	val weight: Long,

	@Enumerated(EnumType.STRING)
	@Column(name = "weight_unit", nullable = false, columnDefinition = "product_width_enum")
	@Type(type = "pgsql_enum")
	val weightUnit: ProductWeightUnit,

	@CreationTimestamp
	@Column(name = "created_at")
	var createdAt: Instant? = null,

	@UpdateTimestamp
	@Column(name = "updated_at")
	var updatedAt: Instant? = null,

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "productId", cascade = [CascadeType.ALL])
	var gallery: MutableList<ProductMediaEntity> = mutableListOf(),

	@OneToOne(mappedBy = "product")
	val order: InventoryEntity? = null,
) {
	companion object {
		const val GET_BY_PRODUCT_ID = "ProductEntity.get_by_product_id"
	}
}

fun ProductEntity.toProduct() = Product(
	productId = productId!!,
	name = name,
	slug = slug,
	sku = sku,
	description = description,
	price = price,
	currency = currency,
	tags = tags,
	inStock = null,
	quantityInStock = 0,
	dimensions = ProductDimensions(
		size = ProductSize(
			width = width,
			length = length,
			height = height,
			unit = widthUnit
		),
		weight = ProductWeight(
			value = weight,
			unit = weightUnit
		)
	),
	gallery = gallery.map {
		ProductMedia(
			mediaId = it.mediaId!!,
			productId = it.productId,
			original = it.original,
			thumbnail = it.thumbnail,
			isPrimary = it.isPrimary,
			type = it.type
		)
	}
)

fun CreateProduct.toProductEntity(slug: String, sku: String) = ProductEntity(
	name = name,
	slug = slug,
	sku = sku,
	description = description,
	price = price,
	currency = currency,
	tags = tags,
	height = dimensions.size.height,
	width = dimensions.size.width,
	length = dimensions.size.length,
	widthUnit = dimensions.size.unit,
	weight = dimensions.weight.value,
	weightUnit = dimensions.weight.unit,
)