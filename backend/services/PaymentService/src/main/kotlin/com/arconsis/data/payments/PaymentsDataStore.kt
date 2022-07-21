package com.arconsis.data.payments

import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentStatus
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsDataStore {
//    fun createPayment(payment: Payment, session: Mutiny.Session): Uni<Payment> {
//        val paymentEntity = payment.toPaymentEntity()
//        return session.persist(paymentEntity)
//            .map { paymentEntity.toPayment() }
//    }

    fun createPayment(payment: CreatePayment, session: Mutiny.Session): Uni<Payment> {
        val paymentEntity = payment.toPaymentEntity(PaymentStatus.IN_PROGRESS)
        return session.persist(paymentEntity)
            .map { paymentEntity.toPayment() }
    }
}