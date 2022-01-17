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
	var id: Long?,
	@NotNull(message = "error.employee.firstName.null")
	@NotBlank(message = "error.employee.firstName.blank")
	@Size(max = NAME_MAX_SIZE, message = "error.employee.firstName.size.invalid")
	var firstName: String?,
	@NotNull(message = "error.employee.lastName.null")
	@NotBlank(message = "error.employee.lastName.blank")
	@Size(max = NAME_MAX_SIZE, message = "error.employee.lastName.size.invalid")
	var lastName: String?,
	@NotNull(message = "error.employee.email.null")
	@NotBlank(message = "error.employee.email.blank")
	@Email(message = "error.employee.email.format.invalid")
	@Size(max = EMAIL_MAX_SIZE, message = "error.employee.email.size.invalid")
	var email: String?,
	@NotNull(message = "error.employee.position.null")
	var position: Role = Role.MANAGER,
	@NotNull(message = "error.employee.password.null")
	@NotBlank(message = "error.employee.password.blank")
	@Size(min = PASSWORD_MIN_SIZE, message = "error.employee.password.size.invalid")
	var password: String? = null,
)