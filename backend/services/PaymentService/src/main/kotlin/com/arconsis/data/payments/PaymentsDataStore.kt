package com.arconsis.data.payments

import com.arconsis.domain.payments.Payment
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsDataStore : PanacheRepository<PaymentEntity> {
    fun createPayment(payment: Payment): Uni<Payment> {
        val paymentEntity = payment.toPaymentEntity()
        return persist(paymentEntity)
            .map {
                it.toPayment()
            }
    }
}