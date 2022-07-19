package com.arconsis.domain.inventory

import com.arconsis.data.inventory.InventoryRepository
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class InventoryService(
    private val inventoryRepository: InventoryRepository,
    private val sessionFactory: Mutiny.SessionFactory,
) {
    fun getInventory(id: UUID): Uni<Inventory?> {
        return sessionFactory.withTransaction { session, _ ->
            inventoryRepository.getInventory(id, session)
        }
    }

    fun createInventory(createInventory: CreateInventory): Uni<Inventory> {
        return sessionFactory.withTransaction { session, _ ->
            inventoryRepository.createInventory(createInventory, session)
        }
    }
}