package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.models.KeycloakUser

fun Employee.toDto(): EmployeeDto = EmployeeDto(id, firstName, lastName, email, position, deleted)

fun EmployeeDto.toEntity(): Employee = Employee(firstName!!, lastName!!, email!!, position).apply { id }

fun Employee.toRegistrationEmployeeDto(): EmployeeRegistrationDto =
    EmployeeRegistrationDto(id, firstName, lastName, email, position)

fun EmployeeRegistrationDto.toEntity(): Employee =
    Employee(firstName!!, lastName!!, email!!, position).apply { id }

fun EmployeeDto.toEmployeeRegistrationDto(): EmployeeRegistrationDto =
    EmployeeRegistrationDto(id, firstName!!, lastName!!, email!!, position)

fun EmployeeRegistrationDto.toKeyCloakUser() = KeycloakUser(
    username = "${firstName}.${lastName}",
    email = email!!,
    password = password!!,
    firstName = firstName!!,
    lastName = lastName!!,
    roles = listOf(position.toString())
)

fun EmployeeDto.toKeyCloakUser() = KeycloakUser(
    username = "${firstName}.${lastName}",
    email = email!!,
    firstName = firstName!!,
    lastName = lastName!!,
    roles = listOf(position.toString()),
    password = ""
)
