package com.arconsis.data.checkoutevents

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.domain.checkoutevents.CheckoutEvent
import com.arconsis.domain.checkoutevents.CreateCheckoutEvent
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity(name = "checkout_events")
@TypeDef(
	name = "pgsql_enum",
	typeClass = PostgreSQLEnumType::class
)
class CheckoutEventEntity(
	@Id
	@GeneratedValue
	@Column(name = "event_id")
	var eventId: UUID? = null,

	// FK
	@Column(name = "payment_id")
	var paymentId: UUID,

	// TODO: make better schema to handle events
	@Column(name = "metadata")
	var metadata: String,

	@Column(name = "type")
	var type: String,

	@CreationTimestamp
	@Column(name = "created_at")
	var createdAt: Instant? = null,

	@UpdateTimestamp
	@Column(name = "updated_at")
	var updatedAt: Instant? = null,
)

fun CreateCheckoutEvent.toCheckoutEntity() = CheckoutEventEntity(
	paymentId = paymentId,
	metadata = metadata,
	type = type
)

fun CheckoutEventEntity.toCheckoutEvent() = CheckoutEvent(
	eventId = eventId!!,
	paymentId = paymentId,
	metadata = metadata,
	type = type
)