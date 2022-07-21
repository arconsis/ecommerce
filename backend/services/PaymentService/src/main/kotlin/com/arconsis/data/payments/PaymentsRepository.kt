package com.arconsis.data.payments

import com.arconsis.domain.payments.*
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.reactive.mutiny.Mutiny.Session
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRepository(private val paymentsDataStore: PaymentsDataStore) {
//    fun createPayment(eventId: UUID, createPayment: CreatePayment, session: Session): Uni<Payment> {
//        // https://github.com/quarkusio/quarkus/issues/23804
//        // TODO: replace next line with paymentsRemoteStore.createPayment()
//        val payment = createPayment.toPayment(transactionId = UUID.randomUUID(), status = PaymentStatus.SUCCEED)
//        return paymentsDataStore.createPayment(payment, session)
//    }
    fun createPayment(eventId: UUID, createPayment: CreatePayment, session: Session): Uni<Payment> {
        return paymentsDataStore.createPayment(createPayment, session)
    }
}