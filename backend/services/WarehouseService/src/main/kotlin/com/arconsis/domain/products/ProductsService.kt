package com.arconsis.domain.products

import com.arconsis.data.common.asPair
import com.arconsis.data.common.toUni
import com.arconsis.data.inventory.InventoryRepository
import com.arconsis.data.products.ProductsRepository
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProductsService(
	private val productsRepository: ProductsRepository,
	private val inventoryRepository: InventoryRepository,
	private val sessionFactory: Mutiny.SessionFactory,
) {
	fun getProduct(productId: UUID): Uni<Product?> {
		return sessionFactory.withTransaction { session, _ ->
			productsRepository.getProduct(productId, session)
				.flatMap { product ->
					Uni.combine().all().unis(
						inventoryRepository.getInventoryByProductId(productId, session),
						product.toUni(),
					).asPair()
				}
				.map { (productStock, product) ->
					product?.copy(isOrderable = productStock?.stock != null && productStock.stock > 0)
				}
		}
	}

	fun createProduct(newProduct: CreateProduct): Uni<Product> {
		return sessionFactory.withTransaction { session, _ ->
			productsRepository.createProduct(newProduct, session)
		}
	}
}