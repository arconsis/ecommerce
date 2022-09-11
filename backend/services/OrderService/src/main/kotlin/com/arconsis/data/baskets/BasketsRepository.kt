package com.arconsis.data.baskets

import com.arconsis.domain.baskets.Basket
import com.arconsis.domain.baskets.CreateBasket
import com.arconsis.domain.baskets.CreateBasketItem
import com.arconsis.presentation.http.baskets.dto.AddPaymentMethodDto
import com.arconsis.presentation.http.baskets.dto.AddShippingProviderDto
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BasketsRepository(private val basketsDataStore: BasketsDataStore) {
	fun createBasket(newBasket: CreateBasket, session: Mutiny.Session): Uni<Basket> {
		return basketsDataStore.createBasket(newBasket, session)
	}

	fun getBasket(basketId: UUID, session: Mutiny.Session): Uni<Basket?> {
		return basketsDataStore.getBasket(basketId, session)
	}

	fun updateBasketPaymentMethod(
		basketId: UUID,
		newPaymentMethod: AddPaymentMethodDto,
		session: Mutiny.Session
	): Uni<Boolean> {
		return basketsDataStore.updateBasketPaymentMethod(basketId, newPaymentMethod, session)
	}

	fun updateBasketShippingProvider(
		basketId: UUID,
		newShippingProvider: AddShippingProviderDto,
		session: Mutiny.Session
	): Uni<Boolean> {
		return basketsDataStore.updateBasketShippingProvider(basketId, newShippingProvider, session)
	}

	fun addBasketItem(
		basketId: UUID,
		items: List<CreateBasketItem>,
		session: Mutiny.Session
	): Uni<Boolean> {
		return basketsDataStore.addBasketItem(basketId, items, session)
	}
}