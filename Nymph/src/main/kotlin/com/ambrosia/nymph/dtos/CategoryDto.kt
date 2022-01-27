package com.ambrosia.nymph.dtos

import com.ambrosia.nymph.constants.NAME_MAX_SIZE
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class CategoryDto(
    var id: Long? = null,
    @field:NotNull(message = "error.category.name.null")
    @field:NotBlank(message = "error.category.name.blank")
    @field:Size(max = NAME_MAX_SIZE, message = "error.category.name.size.invalid")
    var name: String?,
    var description: String?,
    var image: String?,
    var deleted: Boolean = false,
)
