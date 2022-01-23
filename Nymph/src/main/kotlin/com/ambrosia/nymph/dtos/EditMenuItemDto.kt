package com.ambrosia.nymph.dtos

import com.ambrosia.nymph.constants.Constants.Companion.PRICE_MIN
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class EditMenuItemDto(
    var id: Long? = null,
    @field:NotNull(message = "error.menuItem.extra.null")
    @field:Min(PRICE_MIN, message = "error.menuItem.extra.negative")
    var extra: Double? = 0.0,
)
