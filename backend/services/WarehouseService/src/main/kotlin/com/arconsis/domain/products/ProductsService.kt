package com.arconsis.domain.products

import com.arconsis.common.toSlug
import com.arconsis.data.common.asPair
import com.arconsis.data.common.toUni
import com.arconsis.data.inventory.InventoryRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.products.ProductsRepository
import com.arconsis.domain.inventory.CreateInventory
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.apache.commons.lang3.RandomStringUtils
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProductsService(
	private val productsRepository: ProductsRepository,
	private val inventoryRepository: InventoryRepository,
	private val outboxEventsRepository: OutboxEventsRepository,
	private val sessionFactory: Mutiny.SessionFactory,
	private val objectMapper: ObjectMapper,
) {

	fun listProducts(offset: Int, limit: Int, search: String?): Uni<Pair<List<Product>, Int>> {
		return productsRepository.listProducts(offset, limit, search)
	}

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
			if (newProduct.gallery.all { !it.isPrimary }) {
				newProduct.gallery[0].isPrimary = true
			}
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
				.flatMap{ (productStock, product) ->
					val createOutboxEvent = product.toCreateOutboxEvent(objectMapper)
					Uni.combine().all().unis(
						// fire event when new product is added
						outboxEventsRepository.createEvent(createOutboxEvent, session),
						product?.copy(inStock = productStock?.stock != null && productStock.stock > 0, quantityInStock = productStock?.stock).toUni()
					).asPair()
				}
				.map { (_, product) ->
					product
				}
		}
	}

	companion object {
		private const val SKU_ID_LENGTH = 8
	}
}
