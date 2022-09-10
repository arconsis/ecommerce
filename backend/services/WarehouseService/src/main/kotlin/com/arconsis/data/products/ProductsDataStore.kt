package com.arconsis.data.products

import com.arconsis.domain.products.CreateProduct
import com.arconsis.domain.products.Product
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny.Session
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProductsDataStore {
	fun getProduct(productId: UUID, session: Session): Uni<Product?> {
		return session.createNamedQuery<ProductEntity>(ProductEntity.GET_BY_PRODUCT_ID)
			.setParameter("productId", productId)
			.singleResultOrNull
			.map { it.toProduct() }
	}

	fun createProduct(newProduct: CreateProduct, slug: String, sku: String, session: Session): Uni<Product> {
		val entity = newProduct.toProductEntity(slug, sku)
		return session.persist(entity)
			.map { entity.toProduct() }
	}
}
