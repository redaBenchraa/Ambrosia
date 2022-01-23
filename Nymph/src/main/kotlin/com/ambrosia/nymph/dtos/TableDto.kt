package com.ambrosia.nymph.dtos

import javax.validation.constraints.NotNull

data class TableDto(
    var id: Long? = null,
    @field:NotNull(message = "error.table.number.null")
    var number: Int?,
    @field:NotNull(message = "error.table.isAvailable.null")
    var isAvailable: Boolean? = true,
)
