package com.ambrosia.nymph.dtos

import com.ambrosia.nymph.constants.OrderStatus

class OrderDto(
    var id: Long? = null,
    var status: OrderStatus = OrderStatus.DRAFT,
    var orderItems: MutableSet<OrderedItemDto> = HashSet(),
)
