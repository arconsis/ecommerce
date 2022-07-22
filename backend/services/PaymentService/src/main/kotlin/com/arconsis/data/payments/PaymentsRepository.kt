package com.arconsis.data.payments

import com.arconsis.domain.payments.*
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny.Session
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRepository(private val paymentsDataStore: PaymentsDataStore) {
    fun createPayment(createPayment: CreatePayment, session: Session): Uni<Payment> {
        return paymentsDataStore.createPayment(createPayment, session)
    }
}