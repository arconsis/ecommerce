package com.arconsis.data.inventory

import com.arconsis.domain.inventory.CreateInventory
import com.arconsis.domain.inventory.Inventory
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class InventoryRepository(private val inventoryDataStore: InventoryDataStore) {

    fun getInventory(id: UUID): Uni<Inventory?> {
        return inventoryDataStore.getInventory(id)
    }

    fun getInventoryByProductId(productId: UUID): Uni<Inventory?> {
        return inventoryDataStore.getInventoryByProductId(productId)
    }

    fun createInventory(createInventory: CreateInventory): Uni<Inventory> {
        return inventoryDataStore.createInventory(createInventory)
    }

    fun reserveProductStock(productId: UUID, stock: Int): Uni<Boolean> {
        return inventoryDataStore.reserveProductStock(productId, stock)
    }

    fun increaseProductStock(productId: UUID, stock: Int): Uni<Boolean> {
        return inventoryDataStore.increaseProductStock(productId, stock)
    }
}