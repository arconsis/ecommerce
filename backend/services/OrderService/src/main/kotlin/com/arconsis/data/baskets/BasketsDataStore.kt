package com.arconsis.data.baskets

import com.arconsis.common.toUni
import com.arconsis.data.basketitems.BasketItemEntity
import com.arconsis.domain.baskets.Basket
import com.arconsis.domain.baskets.CreateBasket
import com.arconsis.domain.baskets.CreateBasketItem
import com.arconsis.presentation.http.baskets.dto.AddPaymentMethodDto
import com.arconsis.presentation.http.baskets.dto.AddShippingProviderDto
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import org.jboss.logging.Logger
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BasketsDataStore(private val logger: Logger) {
	fun createBasket(newBasket: CreateBasket, session: Mutiny.Session): Uni<Basket> {
		val basketEntity = newBasket.toBasketEntity()
		return session.persist(basketEntity)
			.flatMap {
				basketEntity.itemEntities = newBasket.items.map {
					BasketItemEntity(
						basketId = basketEntity.basketId!!,
						productId = it.productId,
						quantity = it.quantity,
						price = it.price,
						currency = it.currency,
						thumbnail = it.thumbnail,
						name = it.name,
						description = it.description
					)
				}.toMutableList()
				session.persist(basketEntity)
			}
			.map { basketEntity.toBasket() }
	}

	fun getBasket(basketId: UUID, session: Mutiny.Session): Uni<Basket?> {
		return session.find(BasketEntity::class.java, basketId)
			.map { entity -> entity.toBasket() }
	}

	fun updateBasketPaymentMethod(basketId: UUID, newPaymentMethod: AddPaymentMethodDto, session: Mutiny.Session): Uni<Boolean> {
		return session.createNamedQuery<BasketEntity>(BasketEntity.UPDATE_BASKET_PAYMENT_METHOD)
			.setParameter("pspToken", newPaymentMethod.pspToken)
			.setParameter("paymentMethodType", newPaymentMethod.paymentMethodType)
			.setParameter("basketId", basketId)
			.executeUpdate()
			.map {
					updatedRows -> updatedRows == 1
			}
	}

	fun updateBasketShippingProvider(
		basketId: UUID,
		newShippingProvider: AddShippingProviderDto,
		session: Mutiny.Session
	): Uni<Boolean> {
		return session.createNamedQuery<BasketEntity>(BasketEntity.UPDATE_BASKET_SHIPPING_PROVIDER)
			.setParameter("externalShippingProviderId", newShippingProvider.providerId)
			.setParameter("carrierAccount", newShippingProvider.carrierAccount)
			.setParameter("shippingProviderName", newShippingProvider.name)
			.setParameter("shippingPrice", newShippingProvider.price)
			.setParameter("basketId", basketId)
			.executeUpdate()
			.map {
					updatedRows -> updatedRows == 1
			}
	}

	fun addBasketItem(
		basketId: UUID,
		items: List<CreateBasketItem>,
		session: Mutiny.Session
	): Uni<Boolean> {
		val unis = items.map {
			BasketItemEntity(
				basketId = basketId,
				productId = it.productId,
				quantity = it.quantity,
				price = it.price,
				currency = it.currency,
				thumbnail = it.thumbnail,
				name = it.name,
				description = it.description
			)
		}.map { entity ->
			session.persist(entity)
		}
		return Uni.combine()
			.all().unis<Uni<List<Void>>>(unis)
			.combinedWith { listOfResponses: List<*> ->
				listOfResponses
			}.map {
				true
			}
			.onFailure {
				logger.error("Error when trying to add items in basket $basketId", it)
				false
			}
			.recoverWithUni { _ ->
				false.toUni()
			}
	}
}