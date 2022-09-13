package com.arconsis.data.productmedia

import com.arconsis.data.common.PostgreSQLEnumType
import com.arconsis.domain.productmedia.ProductMediaType
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity(name = "product_media")
@TypeDef(
	name = "pgsql_enum",
	typeClass = PostgreSQLEnumType::class
)
class ProductMediaEntity(
	@Id
	@GeneratedValue
	@Column(name = "media_id")
	var mediaId: UUID? = null,

	// FK
	@Column(name = "product_id")
	var productId: UUID,

	@Column(nullable = false)
	var original: String,

	@Column(nullable = false)
	val thumbnail: String,

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, columnDefinition = "product_media")
	@Type(type = "pgsql_enum")
	var type: ProductMediaType,

	@Column(name = "is_primary", nullable = false)
	val isPrimary: Boolean,

	@CreationTimestamp
	@Column(name = "created_at")
	var createdAt: Instant? = null,

	@UpdateTimestamp
	@Column(name = "updated_at")
	var updatedAt: Instant? = null,
)