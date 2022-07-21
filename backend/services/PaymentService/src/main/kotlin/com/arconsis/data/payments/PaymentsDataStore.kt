package com.arconsis.data.payments

import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentStatus
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsDataStore {
    fun createPayment(payment: CreatePayment, session: Mutiny.Session): Uni<Payment> {
        val paymentEntity = payment.toPaymentEntity(PaymentStatus.IN_PROGRESS)
        return session.persist(paymentEntity)
            .map { paymentEntity.toPayment() }
    }

    fun getPayment(paymentId: UUID, session: Mutiny.Session): Uni<Payment?> {
        return session.find(PaymentEntity::class.java, paymentId)
            .map { entity -> entity.toPayment() }
    }

    fun getPaymentByOrderId(orderId: UUID, session: Mutiny.Session): Uni<Payment?> {
        return session.createQuery<PaymentEntity>("SELECT v FROM payments v WHERE v.orderId = '$orderId'")
            .singleResultOrNull
            .map { entity ->
                entity.toPayment()
            }
    }

    fun updatePaymentStatus(paymentId: UUID, status: PaymentStatus, session: Mutiny.Session): Uni<Payment> {
        return session.find(PaymentEntity::class.java, paymentId)
            .map { entity ->
                entity.status = status
                entity
            }
            .onItem().ifNotNull().transformToUni { entity ->
                session.merge(entity)
            }
            .map { updatedEntity -> updatedEntity.toPayment() }
    }
}