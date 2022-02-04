package com.ambrosia.nymph.dtos

class AddOrderDto(var items: Set<ItemsToOrder> = HashSet())

class ItemsToOrder(var id: Long, var description: String? = null)
