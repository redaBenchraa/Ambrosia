package com.ambrosia.nymph.dtos

import com.ambrosia.nymph.constants.Constants
import com.ambrosia.nymph.constants.Role
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class EmployeeDto(
	var id: String?,
	@NotNull(message = "error.employee.firstName.null")
	@NotBlank(message = "error.employee.firstName.blank")
	@Size(max = Constants.NAME_MAX_SIZE, message = "error.employee.firstName.invalidSize")
	var firstName: String,
	@NotNull(message = "error.employee.lastName.null")
	@NotBlank(message = "error.employee.lastName.blank")
	@Size(max = Constants.NAME_MAX_SIZE, message = "error.employee.lastName.invalidSize")
	var lastName: String,
	@NotNull(message = "error.employee.position.null")
	var position: Role = Role.MANAGER,
	var password: String? = null,
)