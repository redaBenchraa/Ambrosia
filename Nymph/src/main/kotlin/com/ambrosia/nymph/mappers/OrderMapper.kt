package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.OrderDto
import com.ambrosia.nymph.entities.Order

fun Order.toDto(): OrderDto =
    OrderDto(id, orderItems = orderItems.stream().map { it.toDto() }.toList().toMutableSet(), confirmed, approved)
