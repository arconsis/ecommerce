package com.arconsis.domain.products

import com.arconsis.data.common.asPair
import com.arconsis.data.common.toUni
import com.arconsis.data.inventory.InventoryRepository
import com.arconsis.data.products.ProductsRepository
import com.arconsis.domain.inventory.Inventory
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.asUni
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class ProductsService(
	private val productsRepository: ProductsRepository,
	private val inventoryRepository: InventoryRepository,
) {
	@ReactiveTransactional
	fun getProduct(productId: UUID): Uni<Product?> {
		return productsRepository.getProduct(productId)
			.flatMap { product ->
				Uni.combine().all().unis(
					inventoryRepository.getInventoryByProductId(productId),
					product.toUni(),
				).asPair()
			}
			.map { (productStock, product) ->
				product?.copy(isOrderable = productStock?.stock != null && productStock.stock > 0)
			}
	}

	@ReactiveTransactional
	fun createProduct(newProduct: CreateProduct): Uni<Product> {
		return productsRepository.createProduct(newProduct)

		// return productsRepository.createProduct(newProduct)
	}
}