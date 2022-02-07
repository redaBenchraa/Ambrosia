package com.ambrosia.nymph.exceptions

import com.ambrosia.nymph.constants.OrderStatus

class OrderWorkflowException(
    val initialOrderStatus: OrderStatus,
    val orderStatus: OrderStatus,
    val parameters: MutableMap<String, Any>
) :
    RuntimeException()
