package com.arconsis.data.payments

import com.arconsis.domain.orders.Order
import com.stripe.model.Charge
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRepository(
    private val paymentsRemoteStore: PaymentsRemoteStore,
) {
    // TODO: map Charge to our model
    fun charge(order: Order): Uni<Charge?> = paymentsRemoteStore.charge(order)
}