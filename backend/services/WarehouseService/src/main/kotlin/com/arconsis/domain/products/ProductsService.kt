package com.arconsis.domain.products

import com.arconsis.common.toSlug
import com.arconsis.data.common.asPair
import com.arconsis.data.common.toUni
import com.arconsis.data.inventory.InventoryRepository
import com.arconsis.data.products.ProductsRepository
import com.arconsis.domain.inventory.CreateInventory
import io.smallrye.mutiny.Uni
import org.apache.commons.lang3.RandomStringUtils
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
					product?.copy(inStock = productStock?.stock != null && productStock.stock > 0, quantityInStock = productStock?.stock)
				}
		}
	}

	fun createProduct(newProduct: CreateProduct): Uni<Product> {
		return sessionFactory.withTransaction { session, _ ->
			val slug = newProduct.name.toSlug()
			val sku = RandomStringUtils.randomAlphanumeric(SKU_ID_LENGTH)
			productsRepository.createProduct(newProduct, slug, sku, session)
				.flatMap { product ->
					if (newProduct.quantityInStock != null) {
						Uni.combine().all().unis(
							inventoryRepository.createInventory(CreateInventory(product.productId, newProduct.quantityInStock), session),
							product.toUni(),
						).asPair()
					} else {
						Uni.combine().all().unis(
							null.toUni(),
							product.toUni(),
						).asPair()
					}
				}
				.map { (productStock, product) ->
					product?.copy(inStock = productStock?.stock != null && productStock.stock > 0, quantityInStock = productStock?.stock)
				}
		}
	}

	companion object {
		private const val SKU_ID_LENGTH = 8
	}
}