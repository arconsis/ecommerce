package com.arconsis.domain.baskets

import com.arconsis.data.baskets.BasketsRepository
import com.arconsis.presentation.http.baskets.dto.CreateBasketDto
import com.arconsis.presentation.http.baskets.dto.toCreateBasket
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BasketsService(
	val basketsRepository: BasketsRepository,
	private val sessionFactory: Mutiny.SessionFactory,
) {
	fun createBasket(basketDto: CreateBasketDto): Uni<Basket> {
		val newBasket = basketDto.toCreateBasket()
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