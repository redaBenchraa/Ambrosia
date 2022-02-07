package com.ambrosia.nymph.dtos

import com.ambrosia.nymph.constants.DEFAULT_DOUBLE_VALUE
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class EditMenuItemDto(
    var id: Long? = null,
    @field:NotNull(message = "error.menuItem.extra.null")
    @field:Min(DEFAULT_DOUBLE_VALUE.toLong(), message = "error.menuItem.extra.negative")
    var extra: Double? = DEFAULT_DOUBLE_VALUE,
)
