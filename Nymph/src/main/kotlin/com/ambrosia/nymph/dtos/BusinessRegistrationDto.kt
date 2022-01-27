package com.ambrosia.nymph.dtos

import com.ambrosia.nymph.constants.Currency
import com.ambrosia.nymph.constants.EMAIL_MAX_SIZE
import com.ambrosia.nymph.constants.NAME_MAX_SIZE
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class BusinessRegistrationDto(
    var id: Long? = null,
    @field:NotNull(message = "error.business.name.null")
    @field:NotBlank(message = "error.business.name.blank")
    @field:Size(max = NAME_MAX_SIZE, message = "error.business.name.size.invalid")
    var name: String?,
    @field:NotNull(message = "error.business.phoneNumber.null")
    @field:NotBlank(message = "error.business.phoneNumber.blank")
    var phoneNumber: String?,
    @field:NotNull(message = "error.business.email.null")
    @field:NotBlank(message = "error.business.email.blank")
    @field:Email(message = "error.business.email.format.invalid")
    @field:Size(max = EMAIL_MAX_SIZE, message = "error.business.email.size.invalid")
    var email: String?,
    var description: String?,
    var slogan: String?,
    var logo: String?,
    var location: String?,
    var currency: String? = Currency.EUR.name,
    var isAvailable: Boolean? = true,
    @field:Valid
    @field:NotNull(message = "error.business.employee.null")
    var employee: EmployeeRegistrationDto? = null,
)
