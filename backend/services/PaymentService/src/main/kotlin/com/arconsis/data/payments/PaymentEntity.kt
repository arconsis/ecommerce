package com.arconsis.data.payments

import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity(name = "payments")
class PaymentEntity(
    @Id
    @GeneratedValue
    @Column(name = "payment_id")
    var paymentId: UUID? = null,

    // FK
    @Column(name = "checkout_id", nullable = false, unique = false)
    var checkoutId: UUID,

    @Column(name = "psp_reference_id", nullable = false, unique = true)
    var pspReferenceId: String,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
)

fun PaymentEntity.toPayment() = Payment(
    paymentId = paymentId!!,
    pspReferenceId = pspReferenceId,
    checkoutId = checkoutId
)

fun CreatePayment.toPaymentEntity() = PaymentEntity(
    pspReferenceId = pspReferenceId,
    checkoutId = checkoutId
)