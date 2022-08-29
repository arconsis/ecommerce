package com.arconsis.data.checkouts

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.domain.checkouts.Checkout
import com.arconsis.domain.checkouts.CheckoutStatus
import com.arconsis.domain.checkouts.CreateCheckout
import com.arconsis.domain.orders.SupportedCurrencies
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import javax.persistence.*

@TypeDef(
    name = "pgsql_enum",
    typeClass = PostgreSQLEnumType::class
)
@Entity(name = "checkouts")
class CheckoutEntity(
    @Id
    @GeneratedValue
    @Column(name = "checkout_id")
    var checkoutId: UUID? = null,

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Column(name = "order_id", nullable = false)
    var orderId: UUID,

    // checkout session from PSP
    @Column(name = "psp_token", nullable = false, unique = true)
    var pspToken: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", columnDefinition = "order_status")
    @Type(type = "pgsql_enum")
    var paymentStatus: CheckoutStatus,

    @Column(name = "payment_error_message", nullable = true)
    var paymentErrorMessage: String?,

    @Column(name = "payment_error_code", nullable = true)
    var paymentErrorCode: String?,

    @Column(nullable = false)
    var amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, columnDefinition = "supported_currencies")
    @Type(type = "pgsql_enum")
    var currency: SupportedCurrencies,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
)

fun CheckoutEntity.toCheckout() = Checkout(
    checkoutId = checkoutId!!,
    orderId = orderId,
    userId = userId,
    amount = amount,
    currency = currency,
    paymentStatus = paymentStatus,
    paymentErrorMessage = paymentErrorMessage,
    paymentErrorCode = paymentErrorCode,
    pspToken = pspToken,
)

fun CreateCheckout.toCheckoutEntity(paymentStatus: CheckoutStatus) = CheckoutEntity(
    userId = userId,
    orderId = orderId,
    amount = amount,
    currency = currency,
    paymentStatus = paymentStatus,
    paymentErrorMessage = paymentErrorMessage,
    paymentErrorCode = paymentErrorCode,
    pspToken = pspToken,
)