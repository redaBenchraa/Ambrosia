package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.entities.Employee


fun Employee.toDto(): EmployeeDto = EmployeeDto(
	id = id,
	firstName = firstName,
	lastName = lastName,
	position = position,
)

fun EmployeeDto.toEntity(): Employee = Employee(
	id = id,
	firstName = firstName,
	lastName = lastName,
	position = position,
)