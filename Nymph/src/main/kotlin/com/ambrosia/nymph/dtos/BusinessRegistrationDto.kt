package com.ambrosia.nymph.dtos

import com.ambrosia.nymph.constants.Constants
import com.ambrosia.nymph.constants.Currency
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class BusinessRegistrationDto(
	var id: Long? = null,
	@NotNull(message = "error.business.name.null")
	@NotBlank(message = "error.business.name.blank")
	@Size(max = Constants.NAME_MAX_SIZE, message = "error.business.name.size.invalid")
	var name: String?,
	@NotNull(message = "error.business.phoneNumber.null")
	@NotBlank(message = "error.business.phoneNumber.blank")
	var phoneNumber: String?,
	@NotNull(message = "error.business.email.null")
	@NotBlank(message = "error.business.email.blank")
	@Email(message = "error.business.email.format.invalid")
	@Size(max = Constants.EMAIL_MAX_SIZE, message = "error.business.email.size.invalid")
	var email: String?,
	var description: String?,
	var slogan: String?,
	var logo: String?,
	var location: String?,
	var currency: String = Currency.EUR.name,
	var employee: EmployeeRegistrationDto? = null,
)