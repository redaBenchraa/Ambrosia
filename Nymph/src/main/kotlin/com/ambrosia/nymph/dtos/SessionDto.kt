package com.ambrosia.nymph.dtos

data class SessionDto(
    var id: Long? = null,
    var isPaid: Boolean? = null,
    var isClosed: Boolean? = null,
    var isApproved: Boolean? = null,
    var orders: Set<OrderDto> = HashSet(),
    var bills: Set<BillDto> = HashSet(),
)
