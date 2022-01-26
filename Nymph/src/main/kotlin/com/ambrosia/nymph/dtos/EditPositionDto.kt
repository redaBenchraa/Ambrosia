package com.ambrosia.nymph.dtos

import com.ambrosia.nymph.constants.Role
import javax.validation.constraints.NotNull

data class EditPositionDto(
    @field:NotNull(message = "error.employee.position.null")
    var position: Role = Role.MANAGER,
)
