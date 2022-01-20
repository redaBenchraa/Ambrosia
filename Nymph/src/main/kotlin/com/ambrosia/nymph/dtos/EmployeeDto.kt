package com.ambrosia.nymph.dtos

import com.ambrosia.nymph.constants.Constants.Companion.EMAIL_MAX_SIZE
import com.ambrosia.nymph.constants.Constants.Companion.NAME_MAX_SIZE
import com.ambrosia.nymph.constants.Role
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class EmployeeDto(
	var id: Long?,
	@field:NotNull(message = "error.employee.firstName.null")
	@field:NotBlank(message = "error.employee.firstName.blank")
	@field:Size(max = NAME_MAX_SIZE, message = "error.employee.firstName.size.invalid")
	var firstName: String?,
	@field:NotNull(message = "error.employee.lastName.null")
	@field:NotBlank(message = "error.employee.lastName.blank")
	@field:Size(max = NAME_MAX_SIZE, message = "error.employee.lastName.size.invalid")
	var lastName: String?,
	@field:NotNull(message = "error.employee.email.null")
	@field:NotBlank(message = "error.employee.email.blank")
	@field:Size(max = EMAIL_MAX_SIZE, message = "error.employee.email.size.invalid")
	var email: String?,
	@field:NotNull(message = "error.employee.position.null")
	var position: Role = Role.MANAGER,
	var deleted: Boolean = false
)