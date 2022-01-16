package com.ambrosia.nymph.dtos

import com.ambrosia.nymph.constants.Constants
import com.ambrosia.nymph.constants.Currency
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class BusinessRegistrationDto(
	var id: String?,
	@NotNull(message = "error.business.name.null")
	@NotBlank(message = "error.business.name.blank")
	@Size(max = Constants.NAME_MAX_SIZE, message = "error.business.name.invalidSize")
	var name: String,
	@NotNull
	@NotBlank(message = "error.business.phoneNumber.blank")
	var phoneNumber: String,
	@NotBlank(message = "error.business.email.blank")
	@Email(message = "error.business.email.invalidFormat")
	@Size(max = Constants.EMAIL_MAX_SIZE, message = "error.business.email.invalidSize")
	var email: String,
	var description: String?,
	var slogan: String?,
	var logo: String?,
	var location: String?,
	var currency: String = Currency.EUR.name,
	var employee: EmployeeDto? = null,
)