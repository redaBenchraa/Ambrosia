package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.constants.Role
import com.ambrosia.nymph.constants.UNDEFINED_VALUE
import com.ambrosia.nymph.dtos.CustomerDto
import com.ambrosia.nymph.dtos.CustomerRegistrationDto
import com.ambrosia.nymph.entities.Customer
import com.ambrosia.nymph.models.KeycloakUser

fun Customer.toDto(): CustomerDto = CustomerDto(id, firstName, lastName, dateOfBirth, email, deleted)

fun Customer.toRegistrationCustomerDto(): CustomerRegistrationDto =
    CustomerRegistrationDto(id, firstName, lastName, email, dateOfBirth)

fun CustomerRegistrationDto.toEntity(): Customer =
    Customer(firstName, lastName, dateOfBirth, email).apply { id }

fun CustomerDto.toCustomerRegistrationDto(): CustomerRegistrationDto =
    CustomerRegistrationDto(id, firstName, lastName, email, dateOfBirth)

fun CustomerRegistrationDto.toKeyCloakUser() = KeycloakUser(
    username = "$firstName.$lastName",
    email = email ?: UNDEFINED_VALUE,
    password = password ?: UNDEFINED_VALUE,
    firstName = firstName ?: UNDEFINED_VALUE,
    lastName = lastName ?: UNDEFINED_VALUE,
    roles = listOf(Role.CUSTOMER.toString())
)

fun CustomerDto.toKeyCloakUser() = KeycloakUser(
    username = "$firstName.$lastName",
    email = email ?: UNDEFINED_VALUE,
    firstName = firstName ?: UNDEFINED_VALUE,
    lastName = lastName ?: UNDEFINED_VALUE,
    roles = listOf(Role.CUSTOMER.toString()),
    password = ""
)
