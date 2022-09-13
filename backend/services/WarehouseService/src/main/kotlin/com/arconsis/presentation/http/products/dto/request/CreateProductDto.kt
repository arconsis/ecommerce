package com.arconsis.presentation.http.products.dto.request

import com.arconsis.domain.orders.SupportedCurrencies
import com.arconsis.domain.productmedia.CreateProductMedia
import com.arconsis.domain.productmedia.ProductMediaType
import com.arconsis.domain.products.CreateProduct
import com.arconsis.domain.products.ProductDimensions
import java.math.BigDecimal
import javax.validation.constraints.NotBlank

data class CreateProductDto(
	@field:NotBlank
	val name: String,
	@field:NotBlank
	val description: String,
	@field:NotBlank
	val price: BigDecimal,
	@field:NotBlank
	val currency: SupportedCurrencies,
	@field:NotBlank
	val tags: String,
	@field:NotBlank
	val dimensions: ProductDimensions,
	val quantityInStock: Int?,
	@field:NotBlank
	val gallery: List<CreateProductMediaDto>
)

data class CreateProductMediaDto(
	@field:NotBlank
	val original: String,
	@field:NotBlank
	val thumbnail: String,
	@field:NotBlank
	val type: ProductMediaType,
	@field:NotBlank
	val isPrimary: Boolean
)

fun CreateProductDto.toCreateProduct() = CreateProduct(
	name = name,
	description = description,
	price = price,
	currency = currency,
	dimensions = dimensions,
	quantityInStock = quantityInStock,
	tags = tags,
	gallery = gallery.map {
		CreateProductMedia(
			original = it.original,
			thumbnail = it.thumbnail,
			type = it.type,
			isPrimary = it.isPrimary
		)
	}
)
