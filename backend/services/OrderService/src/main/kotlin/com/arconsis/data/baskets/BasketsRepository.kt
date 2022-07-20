package com.arconsis.data.baskets

import com.arconsis.domain.baskets.Basket
import com.arconsis.domain.baskets.CreateBasket
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
}