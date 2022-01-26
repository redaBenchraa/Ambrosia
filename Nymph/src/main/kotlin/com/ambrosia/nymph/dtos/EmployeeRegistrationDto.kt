package com.ambrosia.nymph.dtos

import com.ambrosia.nymph.constants.Constants.Companion.EMAIL_MAX_SIZE
import com.ambrosia.nymph.constants.Constants.Companion.NAME_MAX_SIZE
import com.ambrosia.nymph.constants.Constants.Companion.PASSWORD_MIN_SIZE
import com.ambrosia.nymph.constants.Role
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class EmployeeRegistrationDto(
    var id: Long? = null,
    @field:NotNull(message = "error.employee.firstName.null")
    @field:NotBlank(message = "error.employee.firstName.blank")
    @field:Size(max = NAME_MAX_SIZE, message = "error.employee.firstName.size.invalid")
    var firstName: String? = null,
    @field:NotNull(message = "error.employee.lastName.null")
    @field:NotBlank(message = "error.employee.lastName.blank")
    @field:Size(max = NAME_MAX_SIZE, message = "error.employee.lastName.size.invalid")
    var lastName: String? = null,
    @field:NotNull(message = "error.employee.email.null")
    @field:NotBlank(message = "error.employee.email.blank")
    @field:Email(message = "error.employee.email.format.invalid")
    @field:Size(max = EMAIL_MAX_SIZE, message = "error.employee.email.size.invalid")
    var email: String? = null,
    @field:NotNull(message = "error.employee.position.null")
    var position: Role = Role.MANAGER,
    @field:NotNull(message = "error.employee.password.null")
    @field:NotBlank(message = "error.employee.password.blank")
    @field:Size(min = PASSWORD_MIN_SIZE, message = "error.employee.password.size.invalid")
    var password: String? = null,
)
