package com.arconsis.domain.productmedia

import java.util.UUID

data class ProductMedia(
	val mediaId: UUID,
	val productId: UUID,
	val original: String,
	val thumbnail: String,
	val type: ProductMediaType,
	val isPrimary: Boolean
)

enum class ProductMediaType {
	image,
	video
}

data class CreateProductMedia(
	val original: String,
	val thumbnail: String,
	val type: ProductMediaType,
	var isPrimary: Boolean
)
