package com.arconsis.presentation.http.inventory

import com.arconsis.domain.inventory.CreateInventory
import com.arconsis.domain.inventory.Inventory
import com.arconsis.domain.inventory.InventoryService
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*

@ApplicationScoped
@Path("/inventory")
class InventoryResource(private val inventoryService: InventoryService) {

    @GET
    fun getInventory(id: UUID): Uni<Inventory?> {
        return inventoryService.getInventory(id)
    }

    @POST
    fun createInventory(createInventory: CreateInventory): Uni<Inventory> {
        return inventoryService.createInventory(createInventory)
    }
}