package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.OrderDto
import com.ambrosia.nymph.entities.Order

fun Order.toDto(): OrderDto = OrderDto(orderedItems = orderedItems.stream().map { it.toDto() }.toList().toMutableSet())

