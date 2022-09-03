package com.arconsis.data.baskets

import com.arconsis.data.basketitems.BasketItemEntity
import com.arconsis.domain.baskets.Basket
import com.arconsis.domain.baskets.CreateBasket
import com.arconsis.presentation.http.baskets.dto.AddPaymentMethodDto
import com.arconsis.presentation.http.baskets.dto.AddShippingProviderDto
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BasketsDataStore {
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
						productName = it.productName,
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
		return session.createNamedQuery<BasketEntity>(BasketEntity.UPDATE_BASKET_SHIPMENT_PROVIDER)
			.setParameter("externalShipmentProviderId", newShippingProvider.providerId)
			.setParameter("carrierAccount", newShippingProvider.carrierAccount)
			.setParameter("shipmentProviderName", newShippingProvider.name)
			.setParameter("shippingPrice", newShippingProvider.price)
			.setParameter("basketId", basketId)
			.executeUpdate()
			.map {
					updatedRows -> updatedRows == 1
			}
	}
}