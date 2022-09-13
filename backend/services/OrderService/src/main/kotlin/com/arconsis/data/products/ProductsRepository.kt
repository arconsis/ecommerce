package com.arconsis.data.products

import com.arconsis.domain.products.Product
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny.Session
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProductsRepository(private val productsDataStore: ProductsDataStore) {
	fun createProduct(newProduct: Product, session: Session): Uni<Product> {
		return productsDataStore.createProduct(newProduct, session)
	}

	fun getProduct(productId: UUID, session: Session): Uni<Product?> {
		return productsDataStore.getProduct(productId, session)
	}
}