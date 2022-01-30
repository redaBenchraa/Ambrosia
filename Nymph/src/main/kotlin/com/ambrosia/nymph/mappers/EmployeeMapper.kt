package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.constants.UNDEFINED_VALUE
import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.models.KeycloakUser

fun Employee.toDto(): EmployeeDto = EmployeeDto(id, firstName, lastName, email, position, deleted)

fun Employee.toRegistrationEmployeeDto(): EmployeeRegistrationDto =
    EmployeeRegistrationDto(id, firstName, lastName, email, position)

fun EmployeeRegistrationDto.toEntity(): Employee =
    Employee(firstName ?: UNDEFINED_VALUE, lastName ?: UNDEFINED_VALUE, email ?: UNDEFINED_VALUE, position).apply { id }

fun EmployeeDto.toEmployeeRegistrationDto(): EmployeeRegistrationDto =
    EmployeeRegistrationDto(
        id,
        firstName ?: UNDEFINED_VALUE,
        lastName ?: UNDEFINED_VALUE,
        email ?: UNDEFINED_VALUE,
        position
    )

fun EmployeeRegistrationDto.toKeyCloakUser() = KeycloakUser(
    username = "$firstName.$lastName",
    email = email ?: UNDEFINED_VALUE,
    password = password ?: UNDEFINED_VALUE,
    firstName = firstName ?: UNDEFINED_VALUE,
    lastName = lastName ?: UNDEFINED_VALUE,
    roles = listOf(position.toString())
)

fun EmployeeDto.toKeyCloakUser() = KeycloakUser(
    username = "$firstName.$lastName",
    email = email ?: UNDEFINED_VALUE,
    firstName = firstName ?: UNDEFINED_VALUE,
    lastName = lastName ?: UNDEFINED_VALUE,
    roles = listOf(position.toString()),
    password = ""
)
