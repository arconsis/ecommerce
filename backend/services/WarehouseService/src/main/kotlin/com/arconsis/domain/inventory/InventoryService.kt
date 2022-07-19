package com.arconsis.domain.inventory

import com.arconsis.data.inventory.InventoryRepository
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class InventoryService(
    private val inventoryRepository: InventoryRepository,
) {
    fun getInventory(id: UUID): Uni<Inventory?> {
        return inventoryRepository.getInventory(id)
    }

    fun createInventory(createInventory: CreateInventory): Uni<Inventory> {
        return inventoryRepository.createInventory(createInventory)
    }
}