package com.arconsis.data.inventory

import com.arconsis.common.errors.abort
import com.arconsis.data.inventory.InventoryEntity.Companion.PRODUCT_ID
import com.arconsis.data.inventory.InventoryEntity.Companion.STOCK
import com.arconsis.data.products.ProductEntity
import com.arconsis.domain.inventory.CreateInventory
import com.arconsis.domain.inventory.Inventory
import com.arconsis.domain.inventory.InventoryFailureReason
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny.Session
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class InventoryDataStore {
	fun getInventory(inventoryId: UUID, session: Session): Uni<Inventory?> {
		return session.createNamedQuery<InventoryEntity>(InventoryEntity.GET_BY_INVENTORY_ID)
			.setParameter("inventoryId", inventoryId)
			.singleResultOrNull
			.map { it.toInventory() }
	}

	fun getInventoryByProductId(productId: UUID, session: Session): Uni<Inventory?> {
		return session.createNamedQuery<InventoryEntity>(InventoryEntity.GET_BY_PRODUCT_ID)
			.setParameter("productId", productId)
			.singleResultOrNull
			.map { it.toInventory() }
	}

	fun createInventory(createInventory: CreateInventory,session: Session): Uni<Inventory> {
		val productEntity = session.getReference(ProductEntity::class.java, createInventory.productId) ?: abort(InventoryFailureReason.ProductNotFound)
		val inventoryEntity = createInventory.toInventoryEntity(productEntity)
		return session.persist(inventoryEntity)
			.map { inventoryEntity.toInventory() }
	}

	fun reserveProductStock(productId: UUID, stock: Int, session: Session): Uni<Boolean> {
		return session.createNamedQuery<InventoryEntity>(InventoryEntity.UPDATE_PRODUCT_STOCK)
			.setParameter(PRODUCT_ID, productId)
			.setParameter(STOCK, stock)
			.executeUpdate()
			.map { updatedRows -> updatedRows == 1 }
			// TODO: Check if we need to handle only the update stock constraint error here
			//.onFailure().recoverWithItem(false)
	}

	fun increaseProductStock(productId: UUID, stock: Int, session: Session): Uni<Boolean> {
		return session.createNamedQuery<InventoryEntity>(InventoryEntity.UPDATE_PRODUCT_STOCK)
			.setParameter(PRODUCT_ID, productId)
			.setParameter(STOCK, stock)
			.executeUpdate()
			.map { updatedRows -> updatedRows == 1 }
			// TODO: Check if we need to handle only the update stock constraint error here
			.onFailure().recoverWithItem(false)
	}
}