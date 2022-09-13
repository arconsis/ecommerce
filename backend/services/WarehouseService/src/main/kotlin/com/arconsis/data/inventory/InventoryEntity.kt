package com.arconsis.data.inventory

import com.arconsis.data.inventory.InventoryEntity.Companion.GET_BY_INVENTORY_ID
import com.arconsis.data.inventory.InventoryEntity.Companion.GET_BY_PRODUCT_ID
import com.arconsis.data.inventory.InventoryEntity.Companion.INCREASE_PRODUCT_STOCK
import com.arconsis.data.inventory.InventoryEntity.Companion.PRODUCT_ID
import com.arconsis.data.inventory.InventoryEntity.Companion.STOCK
import com.arconsis.data.inventory.InventoryEntity.Companion.UPDATE_PRODUCT_STOCK
import com.arconsis.data.products.ProductEntity
import com.arconsis.domain.inventory.CreateInventory
import com.arconsis.domain.inventory.Inventory
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*

@NamedQueries(
    NamedQuery(
        name = UPDATE_PRODUCT_STOCK,
        query = """
            update inventory i
            set i.stock = i.stock - :$STOCK
            where i.productId = :$PRODUCT_ID
        """
    ),
    NamedQuery(
        name = INCREASE_PRODUCT_STOCK,
        query = """
            update inventory i
            set i.stock = i.stock + :$STOCK
            where i.productId = :$PRODUCT_ID
        """
    ),
    NamedQuery(
        name = GET_BY_PRODUCT_ID,
        query = """
            select i from inventory i
			where i.productId = :productId
        """
    ),
    NamedQuery(
        name = GET_BY_INVENTORY_ID,
        query = """
            select i from inventory i
			where i.inventoryId = :inventoryId
        """
    )
)
@Entity(name = "inventory")
class InventoryEntity(
    @Id
    @GeneratedValue
    @Column(name = "inventory_id")
    var inventoryId: UUID? = null,

    // FK
    @Column(name = "product_id", unique = true, insertable = false, updatable = false)
    var productId: UUID,

    @Column(nullable = false)
    var stock: Int,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    val product: ProductEntity
) {
    companion object {
        const val GET_BY_INVENTORY_ID = "InventoryEntity.get_by_inventory_id"
        const val GET_BY_PRODUCT_ID = "InventoryEntity.get_by_product_id"
        const val UPDATE_PRODUCT_STOCK = "InventoryEntity.update_product_stock"
        const val INCREASE_PRODUCT_STOCK = "InventoryEntity.increase_product_stock"
        const val PRODUCT_ID = "product_id"
        const val STOCK = "stock"
        const val INVENTORY_ID = "inventory_id"
    }
}

fun CreateInventory.toInventoryEntity(product: ProductEntity) = InventoryEntity(
    productId = productId,
    stock = stock,
    product = product
)

fun InventoryEntity.toInventory() = Inventory(
    inventoryId = inventoryId!!,
    productId = productId,
    stock = stock,
)