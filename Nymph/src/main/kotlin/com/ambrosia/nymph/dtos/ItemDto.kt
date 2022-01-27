package com.ambrosia.nymph.dtos

import com.ambrosia.nymph.constants.NAME_MAX_SIZE
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class ItemDto(
    var id: Long?,
    @field:NotNull(message = "error.item.name.null")
    @field:NotBlank(message = "error.item.name.blank")
    @field:Size(max = NAME_MAX_SIZE, message = "error.item.name.size.invalid")
    var name: String?,
    var description: String?,
    var image: String?,
    @field:NotNull(message = "error.item.price.null")
    @field:Min(0, message = "error.item.price.negative")
    var price: Double?,
    var onlyForMenu: Boolean? = false,
    var deleted: Boolean = false
)
