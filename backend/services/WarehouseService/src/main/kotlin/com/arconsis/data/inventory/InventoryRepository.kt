package com.arconsis.data.inventory

import com.arconsis.domain.inventory.CreateInventory
import com.arconsis.domain.inventory.Inventory
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny.Session
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class InventoryRepository(private val inventoryDataStore: InventoryDataStore) {

    fun getInventory(id: UUID, session: Session): Uni<Inventory?> {
        return inventoryDataStore.getInventory(id, session)
    }

    fun getInventoryByProductId(productId: UUID, session: Session): Uni<Inventory?> {
        return inventoryDataStore.getInventoryByProductId(productId, session)
    }

    fun createInventory(createInventory: CreateInventory, session: Session): Uni<Inventory> {
        return inventoryDataStore.createInventory(createInventory, session)
    }

    fun reserveProductStock(productId: UUID, stock: Int, session: Session): Uni<Boolean> {
        return inventoryDataStore.reserveProductStock(productId, stock, session)
    }

    fun increaseProductStock(productId: UUID, stock: Int, session: Session): Uni<Boolean> {
        return inventoryDataStore.increaseProductStock(productId, stock, session)
    }
}