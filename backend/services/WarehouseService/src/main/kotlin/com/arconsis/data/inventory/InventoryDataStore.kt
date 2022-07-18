package com.arconsis.data.inventory

import com.arconsis.domain.inventory.CreateInventory
import com.arconsis.domain.inventory.Inventory
import com.arconsis.domain.inventory.UpdateInventory
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.NotFoundException

@ApplicationScoped
class InventoryDataStore: PanacheRepository<InventoryEntity> {
	fun getInventory(id: UUID): Uni<Inventory?> {
		return find("id", id)
			.firstResult<InventoryEntity?>()
			.map {
				if (it == null) {
					throw NotFoundException("No results found");
				}
				it.toInventory()
			}
	}

	fun getInventoryByProductId(productId: UUID): Uni<Inventory?> {
		return find("productId", productId)
			.firstResult<InventoryEntity?>()
			.map {
				if (it == null) {
					throw NotFoundException("No results found");
				}
				it.toInventory()
			}
	}

	fun createInventory(createInventory: CreateInventory): Uni<Inventory> {
		val inventoryEntity = createInventory.toInventoryEntity()
		return persist(inventoryEntity)
			.map {
				it.toInventory()
			}
	}

	fun reserveProductStock(productId: UUID, stock: Int): Uni<Boolean> {
		val params: MutableMap<String, Any> = HashMap()
		params["stock"] = stock
		return update("productId", productId, params)
			.map { updatedRows -> updatedRows > 0 }
			// TODO: Check if we need to handle only the update stock constraint error here
			.onFailure().recoverWithItem(false)
	}

	fun increaseProductStock(productId: UUID, stock: Int): Uni<Boolean> {
		val params: MutableMap<String, Any> = HashMap()
		params["stock"] = stock
		return update("productId", productId, params).map {
			it > 0
		}
			// TODO: Check if we need to handle only the update stock constraint error here
			.onFailure().recoverWithItem(false)
	}
}