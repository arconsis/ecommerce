package com.arconsis.domain.baskets

import com.arconsis.data.baskets.BasketsRepository
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BasketsService(
	val basketsRepository: BasketsRepository,
	private val sessionFactory: Mutiny.SessionFactory,
) {
	fun createBasket(newBasket: CreateBasket): Uni<Basket> {
		return sessionFactory.withTransaction { session, _ ->
			basketsRepository.createBasket(newBasket, session)
				.map { it }
		}
	}

	fun getBasket(basketId: UUID): Uni<Basket?> {
		return sessionFactory.withTransaction { session, _ ->
			basketsRepository.getBasket(basketId, session)
				.map { it }
		}
	}
}