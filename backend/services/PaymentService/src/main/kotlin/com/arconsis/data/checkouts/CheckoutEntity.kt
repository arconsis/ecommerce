package com.arconsis.data.checkouts

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.data.payments.PaymentEntity
import com.arconsis.domain.checkouts.Checkout
import com.arconsis.domain.checkouts.CheckoutStatus
import com.arconsis.domain.checkouts.CreateCheckout
import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
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
    @Column(name = "checkout_session_id", nullable = false, unique = true)
    var checkoutSessionId: String,

    // checkout session from PSP
    @Column(name = "checkout_url", nullable = false, unique = true)
    var checkoutUrl: String,

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "order_status")
    @Type(type = "pgsql_enum")
    var status: CheckoutStatus,

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

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    var payment: PaymentEntity? = null
)

fun CheckoutEntity.toCheckout() = Checkout(
    checkoutId = checkoutId!!,
    orderId = orderId,
    userId = userId,
    amount = amount,
    currency = currency,
    status = status,
    checkoutSessionId = checkoutSessionId,
    checkoutUrl = checkoutUrl
)

fun CreateCheckout.toCheckoutEntity(status: CheckoutStatus) = CheckoutEntity(
    userId = userId,
    orderId = orderId,
    amount = amount,
    currency = currency,
    status = status,
    checkoutSessionId = checkoutSessionId,
    checkoutUrl = checkoutUrl,
)