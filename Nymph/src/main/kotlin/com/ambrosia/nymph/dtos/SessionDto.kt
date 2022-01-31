package com.ambrosia.nymph.dtos

data class SessionDto(
    var id: Long? = null,
    var isPaid: Boolean = false,
    var isClosed: Boolean = false,
    var isApproved: Boolean = true,
    var orders: Set<OrderDto> = HashSet(),
    var bills: Set<BillDto> = HashSet(),
)
