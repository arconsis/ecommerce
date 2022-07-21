package com.arconsis.data.payments

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.domain.checkouts.Checkout
import com.arconsis.domain.orders.Order
import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentStatus
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity(name = "payments")
@TypeDef(
    name = "pgsql_enum",
    typeClass = PostgreSQLEnumType::class
)
class PaymentEntity(
    @Id
    @GeneratedValue
    @Column(name = "payment_id")
    var paymentId: UUID? = null,

    @Column(name = "transaction_id", nullable = true)
    var transactionId: UUID? = null,

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Column(name = "order_id", nullable = false)
    var orderId: UUID,

    // TODO: perhaps can be moved to another table 1:1
    // checkout session from PSP
    @Column(name = "checkout_session_id", nullable = false, unique = true)
    var checkoutSessionId: String,

    // checkout session from PSP
    @Column(name = "checkout_url", nullable = false, unique = true)
    var checkoutUrl: String,

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "order_status")
    @Type(type = "pgsql_enum")
    var status: PaymentStatus,

    @Column(nullable = false)
    var amount: BigDecimal,

    @Column(nullable = false)
    var currency: String,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
)

fun PaymentEntity.toPayment() = Payment(
    paymentId = paymentId!!,
    transactionId = transactionId,
    orderId = orderId,
    userId = userId,
    amount = amount,
    currency = currency,
    status = status,
    checkout = Checkout(
        checkoutSessionId = checkoutSessionId,
        checkoutUrl = checkoutUrl
    )
)

//fun Payment.toPaymentEntity() = PaymentEntity(
//    transactionId = transactionId,
//    userId = userId,
//    orderId = orderId,
//    amount = amount,
//    currency = currency,
//    status = status,
//    checkoutSessionId = checkoutSessionId
//)

fun CreatePayment.toPaymentEntity(status: PaymentStatus) = PaymentEntity(
    userId = userId,
    orderId = orderId,
    amount = amount,
    currency = currency,
    status = status,
    checkoutSessionId = checkoutSessionId,
    checkoutUrl = checkoutUrl,
)