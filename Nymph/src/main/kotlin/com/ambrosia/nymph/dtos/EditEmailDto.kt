package com.ambrosia.nymph.dtos

import com.ambrosia.nymph.constants.Constants
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class EditEmailDto(
    @field:NotNull(message = "error.employee.email.null")
    @field:NotBlank(message = "error.employee.email.blank")
    @field:Email(message = "error.employee.email.format.invalid")
    @field:Size(max = Constants.EMAIL_MAX_SIZE, message = "error.employee.email.size.invalid")
    var email: String? = null,
)
