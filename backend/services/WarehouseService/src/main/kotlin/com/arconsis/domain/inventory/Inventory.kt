package com.arconsis.domain.inventory

import java.util.*

open class CreateInventory(val productId: UUID, val stock: Int)

class Inventory(val inventoryId: UUID, productId: UUID, stock: Int) : CreateInventory(productId, stock)

class UpdateInventory(val inventoryId: UUID, val stock: Int)