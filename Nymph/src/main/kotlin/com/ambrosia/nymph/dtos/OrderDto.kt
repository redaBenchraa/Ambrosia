package com.ambrosia.nymph.dtos

class OrderDto(
    var id: Long? = null,
    var orderItems: MutableSet<OrderedItemDto> = HashSet(),
    var confirmed: Boolean = false,
    var approved: Boolean = false,
)
