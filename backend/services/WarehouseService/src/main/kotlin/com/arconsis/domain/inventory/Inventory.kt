package com.arconsis.domain.inventory

import java.util.*

open class CreateInventory(val productId: UUID, val stock: Int)

class Inventory(val id: UUID, productId: UUID, stock: Int) : CreateInventory(productId, stock)

class UpdateInventory(val id: UUID, val stock: Int)