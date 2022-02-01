package com.ambrosia.nymph.dtos

class OrderDto(
    var orderedItems: MutableSet<OrderedItemDto> = HashSet(),
)

