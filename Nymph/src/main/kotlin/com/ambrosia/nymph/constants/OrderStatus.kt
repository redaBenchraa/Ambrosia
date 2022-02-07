package com.ambrosia.nymph.constants

import com.ambrosia.nymph.constants.OrderStatus.APPROVED
import com.ambrosia.nymph.constants.OrderStatus.CANCELED
import com.ambrosia.nymph.constants.OrderStatus.CONFIRMED
import com.ambrosia.nymph.constants.OrderStatus.DELIVERED
import com.ambrosia.nymph.constants.OrderStatus.DRAFT
import com.ambrosia.nymph.constants.OrderStatus.ONGOING
import com.ambrosia.nymph.constants.OrderStatus.REJECTED

enum class OrderStatus { DRAFT, CONFIRMED, APPROVED, REJECTED, CANCELED, ONGOING, DELIVERED }

val orderStatusWorkflow = mapOf(
    DRAFT to listOf(CONFIRMED, CANCELED),
    CONFIRMED to listOf(APPROVED, REJECTED, CANCELED),
    APPROVED to listOf(ONGOING, REJECTED),
    REJECTED to listOf(APPROVED),
    CANCELED to listOf(),
    ONGOING to listOf(DELIVERED),
    DELIVERED to listOf()
)

fun OrderStatus.canChangeStatusTo(status: OrderStatus): Boolean =
    orderStatusWorkflow[this]?.contains(status) ?: false
