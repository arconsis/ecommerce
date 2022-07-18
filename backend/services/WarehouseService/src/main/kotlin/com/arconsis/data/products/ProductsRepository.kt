package com.arconsis.data.products

import com.arconsis.domain.products.CreateProduct
import com.arconsis.domain.products.Product
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProductsRepository(val productsDataStore: ProductsDataStore) {
	fun getProduct(productId: UUID): Uni<Product> {
		return productsDataStore.getProduct(productId)
	}

	fun createProduct(newProduct: CreateProduct): Uni<Product> {
		return productsDataStore.createProduct(newProduct)
	}
}