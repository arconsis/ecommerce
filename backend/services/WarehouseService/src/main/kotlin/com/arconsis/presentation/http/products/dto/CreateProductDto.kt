package com.arconsis.presentation.http.products.dto

import com.arconsis.domain.products.CreateProduct
import java.math.BigDecimal

data class CreateProductDto(
	val thumbnail: String,
	val productName: String,
	val description: String,
	val price: BigDecimal,
	val currency: String,
	val size: String,
	val category: String,
)

fun CreateProductDto.toCreateProduct() = CreateProduct(
	thumbnail = thumbnail,
	productName = productName,
	description = description,
	price = price,
	currency = currency,
	size = size,
	category = category
)