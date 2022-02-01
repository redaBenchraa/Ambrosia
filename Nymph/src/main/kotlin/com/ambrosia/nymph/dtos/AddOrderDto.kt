package com.ambrosia.nymph.dtos

class AddOrderDto(
    var id: Long? = null,
    var items: Set<ItemsToOrder> = HashSet(),
)

class ItemsToOrder(
    var id: Long,
    var description: String?,
)
