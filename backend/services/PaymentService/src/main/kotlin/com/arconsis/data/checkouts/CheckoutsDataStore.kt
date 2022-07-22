package com.arconsis.data.checkouts

import com.arconsis.domain.checkouts.Checkout
import com.arconsis.domain.checkouts.CheckoutStatus
import com.arconsis.domain.checkouts.CreateCheckout
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CheckoutsDataStore {
	fun createCheckout(newCheckout: CreateCheckout, session: Mutiny.Session): Uni<Checkout> {
		val entity = newCheckout.toCheckoutEntity(CheckoutStatus.PAYMENT_IN_PROGRESS)
		return session.persist(entity)
			.map { entity.toCheckout() }
	}

	fun updateCheckoutStatus(checkoutId: UUID, status: CheckoutStatus, session: Mutiny.Session): Uni<Checkout> {
		return session.find(CheckoutEntity::class.java, checkoutId)
			.map { entity ->
				entity.status = status
				entity
			}
			.onItem().ifNotNull().transformToUni { entity ->
				session.merge(entity)
			}
			.map { updatedEntity -> updatedEntity.toCheckout() }
	}

	fun getCheckoutByOrderId(orderId: UUID, session: Mutiny.Session): Uni<Checkout?> {
		return session.createQuery<CheckoutEntity>("SELECT v FROM checkouts v WHERE v.orderId = '$orderId'")
			.singleResultOrNull
			.map { entity ->
				entity.toCheckout()
			}
	}
}