package com.arconsis.data.products

import com.arconsis.domain.products.CreateProduct
import com.arconsis.domain.products.Product
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProductsRepository(private val productsDataStore: ProductsDataStore) {
	fun getProduct(productId: UUID, session: Mutiny.Session): Uni<Product?> {
		return productsDataStore.getProduct(productId, session)
	}

	fun createProduct(newProduct: CreateProduct, slug: String, sku: String, session: Mutiny.Session): Uni<Product> {
		return productsDataStore.createProduct(newProduct, slug, sku, session)
	}

	fun listProducts(offset: Int, limit: Int, search: String?): Uni<Pair<List<Product>, Int>> {
		return productsDataStore.listProducts(offset, limit, search)
	}
}