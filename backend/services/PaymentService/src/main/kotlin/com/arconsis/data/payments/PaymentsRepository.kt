package com.arconsis.data.payments

import com.arconsis.domain.payments.*
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny.Session
import org.jboss.logging.Logger
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRepository(private val paymentsDataStore: PaymentsDataStore) {
    fun createPayment(eventId: UUID, createPayment: CreatePayment, session: Session): Uni<Payment> {
        return paymentsDataStore.createPayment(createPayment, session)
    }

    fun getPaymentByOrderId(orderId: UUID, session: Session): Uni<Payment?> {
        return paymentsDataStore.getPaymentByOrderId(orderId, session)
    }

    fun updatePaymentStatus(paymentId: UUID, status: PaymentStatus, session: Session): Uni<Payment> {
        return paymentsDataStore.updatePaymentStatus(paymentId, status, session)
    }
}