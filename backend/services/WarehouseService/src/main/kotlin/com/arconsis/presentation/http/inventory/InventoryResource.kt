package com.arconsis.presentation.http.inventory

import com.arconsis.domain.inventory.CreateInventory
import com.arconsis.domain.inventory.Inventory
import com.arconsis.domain.inventory.InventoryService
import com.arconsis.domain.inventory.UpdateInventory
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*

@ApplicationScoped
@Path("/inventory")
class InventoryResource(private val inventoryService: InventoryService) {

    @GET
    @ReactiveTransactional
    fun getInventory(id: UUID): Uni<Inventory?> {
        return inventoryService.getInventory(id)
    }

    @POST
    @ReactiveTransactional
    fun createInventory(createInventory: CreateInventory): Uni<Inventory> {
        return inventoryService.createInventory(createInventory)
    }
}