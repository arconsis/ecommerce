package com.arconsis.data.baskets

import com.arconsis.data.basketitems.BasketItemEntity
import com.arconsis.domain.baskets.Basket
import com.arconsis.domain.baskets.CreateBasket
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

	fun getBasket(orderId: UUID, session: Mutiny.Session): Uni<Basket?> {
		return session.find(BasketEntity::class.java, orderId)
			.map { entity -> entity.toBasket() }
	}
}