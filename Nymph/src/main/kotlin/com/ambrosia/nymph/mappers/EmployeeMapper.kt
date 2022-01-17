package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.entities.Employee


fun Employee.toDto(): EmployeeDto = EmployeeDto(
	id = id,
	firstName = firstName,
	lastName = lastName,
	position = position,
	deleted = deleted,
	email = email
)

fun EmployeeDto.toEntity(): Employee = Employee(
	id = id,
	firstName = firstName,
	lastName = lastName,
	position = position,
	deleted = deleted,
	email = email
)

fun Employee.toRegistrationEmployeeDto(): EmployeeRegistrationDto = EmployeeRegistrationDto(
	id = id,
	firstName = firstName,
	lastName = lastName,
	position = position,
	deleted = deleted,
	email = email
)

fun EmployeeRegistrationDto.toEntity(): Employee = Employee(
	id = id,
	firstName = firstName!!,
	lastName = lastName!!,
	email = email!!,
	position = position,
	deleted = deleted,
)