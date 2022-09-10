package com.arconsis.domain.baskets

import com.arconsis.common.errors.abort
import com.arconsis.data.shippingaddresses.ShippingAddressesRepository
import com.arconsis.data.baskets.BasketsRepository
import com.arconsis.data.products.ProductsRepository
import com.arconsis.domain.orders.OrdersFailureReason
import com.arconsis.domain.products.Product
import com.arconsis.presentation.http.baskets.dto.*
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.BadRequestException

@ApplicationScoped
class BasketsService(
	private val shippingAddressesRepository: ShippingAddressesRepository,
	private val basketsRepository: BasketsRepository,
	private val productsRepository: ProductsRepository,
	private val sessionFactory: Mutiny.SessionFactory,
) {
	fun createBasket(basketDto: CreateBasketDto): Uni<Basket> {
		return sessionFactory.withTransaction { session, _ ->
			val unis: List<Uni<Product>> = basketDto.items.map { item ->
				productsRepository.getProduct(item.productId)
			}
			Uni.combine()
				.all().unis<Uni<List<Product>>>(unis).combinedWith { listOfResponses: List<*> ->
					listOfResponses.map {
						it as Product
					}.toList()
				}
				.flatMap { products ->
					val basketItems = basketDto.items.map { item ->
						products
							.find { it.productId == item.productId }
							.let { product ->
								if (product == null) {
									throw BadRequestException("Product with id: ${item.productId} not found")
								}
								CreateBasketItem(
									productId = item.productId,
									price = product.price,
									currency = product.currency,
									quantity = item.quantity,
									name = product.name,
									description = product.description,
									thumbnail = product.thumbnail,
									slug = product.slug,
									sku = product.sku
								)
							}
					}
					val newBasket = basketDto.toCreateBasket(basketItems)
					basketsRepository.createBasket(newBasket, session)
				}
				.map { it }
		}
	}

	fun getBasket(basketId: UUID): Uni<Basket?> {
		return sessionFactory.withTransaction { session, _ ->
			basketsRepository.getBasket(basketId, session)
				.map { it }
		}
	}

	fun createBasketShippingAddress(basketId: UUID, address: CreateShippingAddressDto): Uni<Basket> {
		val newAddress = address.toCreateShippingAddress(isSelected = true, isBilling = true)
		return sessionFactory.withTransaction { session, _ ->
			shippingAddressesRepository.createShippingAddress(newAddress, basketId, session)
		}.flatMap {
			sessionFactory.withTransaction { session, _ ->
				basketsRepository.getBasket(basketId, session)
			}
				.map { it }
		}
	}

	fun updateBasketPaymentMethod(basketId: UUID, newPaymentMethod: AddPaymentMethodDto): Uni<Basket> {
		return sessionFactory.withTransaction { session, _ ->
			basketsRepository.updateBasketPaymentMethod(basketId, newPaymentMethod, session)
				.flatMap { isUpdated ->
					if (!isUpdated) abort(OrdersFailureReason.BasketNotFound)
					basketsRepository.getBasket(basketId, session)
				}
				.map { it }
		}
	}

	fun updateBasketShippingProvider(basketId: UUID, newShippingProvider: AddShippingProviderDto): Uni<Basket> {
		return sessionFactory.withTransaction { session, _ ->
			basketsRepository.updateBasketShippingProvider(basketId, newShippingProvider, session)
				.flatMap { isUpdated ->
					if (!isUpdated) abort(OrdersFailureReason.BasketNotFound)
					basketsRepository.getBasket(basketId, session)
				}
				.map { it }
		}
	}
}