package com.ambrosia.nymph.constants

import com.ambrosia.nymph.constants.OrderStatus.APPROVED
import com.ambrosia.nymph.constants.OrderStatus.CANCELED
import com.ambrosia.nymph.constants.OrderStatus.CONFIRMED
import com.ambrosia.nymph.constants.OrderStatus.DELIVERED
import com.ambrosia.nymph.constants.OrderStatus.DISAPPROVED
import com.ambrosia.nymph.constants.OrderStatus.DRAFT
import com.ambrosia.nymph.constants.OrderStatus.IN_PROGRESS

enum class OrderStatus { DRAFT, CONFIRMED, APPROVED, DISAPPROVED, CANCELED, IN_PROGRESS, DELIVERED }

val orderStatusWorkflow = mapOf(
    DRAFT to listOf(CONFIRMED),
    CONFIRMED to listOf(APPROVED, DISAPPROVED, CANCELED),
    APPROVED to listOf(IN_PROGRESS, CANCELED),
    DISAPPROVED to listOf(),
    CANCELED to listOf(),
    IN_PROGRESS to listOf(DELIVERED),
    DELIVERED to listOf()
)

fun OrderStatus.canChangeStatusTo(status: OrderStatus): Boolean = orderStatusWorkflow[this]?.contains(status) ?: false