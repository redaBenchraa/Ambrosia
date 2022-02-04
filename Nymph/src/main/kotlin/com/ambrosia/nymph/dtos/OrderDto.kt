package com.ambrosia.nymph.dtos

class OrderDto(
    var id: Long? = null,
    var orderedItems: MutableSet<OrderedItemDto> = HashSet(),
)
