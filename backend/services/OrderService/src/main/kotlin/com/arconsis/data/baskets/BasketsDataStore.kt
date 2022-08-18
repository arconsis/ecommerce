package com.arconsis.data.baskets

import com.arconsis.data.basketitems.BasketItemEntity
import com.arconsis.domain.baskets.Basket
import com.arconsis.domain.baskets.CreateBasket
import com.arconsis.presentation.http.baskets.dto.AddPaymentMethodDto
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
//		return session.find(BasketEntity::class.java, basketId)
//			.map { entity ->
//				entity.pspToken = newPaymentMethod.pspToken
//				entity.paymentMethodType = newPaymentMethod.paymentMethodType
//				entity
//			}
//			.onItem().ifNotNull().transformToUni { entity ->
//				session.merge(entity)
//			}
//			.map { updatedEntity -> updatedEntity.toBasket() }
	}
}