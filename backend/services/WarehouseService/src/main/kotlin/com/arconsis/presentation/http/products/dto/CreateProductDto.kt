package com.arconsis.presentation.http.products.dto

import com.arconsis.domain.orders.SupportedCurrencies
import com.arconsis.domain.products.CreateProduct
import com.arconsis.domain.products.ProductDimensions
import java.math.BigDecimal

data class CreateProductDto(
	val thumbnail: String,
	val name: String,
	val description: String,
	val price: BigDecimal,
	val currency: SupportedCurrencies,
	val tags: String,
	val dimensions: ProductDimensions,
	val quantityInStock: Int?,
)

fun CreateProductDto.toCreateProduct() = CreateProduct(
	thumbnail = thumbnail,
	name = name,
	description = description,
	price = price,
	currency = currency,
	dimensions = dimensions,
	quantityInStock = quantityInStock,
	tags = tags
)