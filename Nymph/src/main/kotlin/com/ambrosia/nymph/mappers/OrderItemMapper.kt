package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.OrderedItemDto
import com.ambrosia.nymph.entities.OrderItem

fun OrderItem.toDto(): OrderedItemDto = OrderedItemDto(
    id, name, description, price, item?.toDto()
)
