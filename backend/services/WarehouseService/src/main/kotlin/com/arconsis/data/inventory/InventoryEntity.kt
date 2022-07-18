package com.arconsis.data.inventory

import com.arconsis.domain.inventory.CreateInventory
import com.arconsis.domain.inventory.Inventory
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity(name = "inventory")
class InventoryEntity(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(name = "product_id", nullable = false)
    var productId: UUID,

    @Column(nullable = false)
    var stock: Int,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
)

fun CreateInventory.toInventoryEntity() = InventoryEntity(
    productId = productId,
    stock = stock,
)

fun InventoryEntity.toInventory() = Inventory(
    id = id!!,
    productId = productId,
    stock = stock,
)