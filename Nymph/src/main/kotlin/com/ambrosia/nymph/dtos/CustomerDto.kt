package com.ambrosia.nymph.dtos

import com.ambrosia.nymph.constants.EMAIL_MAX_SIZE
import com.ambrosia.nymph.constants.NAME_MAX_SIZE
import javax.validation.constraints.Size

data class CustomerDto(
    var id: Long? = null,
    @field:Size(max = NAME_MAX_SIZE, message = "error.customer.firstName.size.invalid")
    var firstName: String? = null,
    @field:Size(max = NAME_MAX_SIZE, message = "error.customer.lastName.size.invalid")
    var lastName: String? = null,
    @field:Size(max = EMAIL_MAX_SIZE, message = "error.customer.email.size.invalid")
    var age: Int? = null,
    @field:Size(max = EMAIL_MAX_SIZE, message = "error.customer.email.size.invalid")
    var email: String? = null,
    var deleted: Boolean = false,
)
