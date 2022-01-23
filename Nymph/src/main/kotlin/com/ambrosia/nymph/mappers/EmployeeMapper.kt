package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.entities.Employee

fun Employee.toDto(): EmployeeDto = EmployeeDto(id, firstName, lastName, email, position, deleted)

fun EmployeeDto.toEntity(): Employee = Employee(id, firstName!!, lastName!!, email!!, position)

fun Employee.toRegistrationEmployeeDto(): EmployeeRegistrationDto =
    EmployeeRegistrationDto(id, firstName, lastName, email, position)

fun EmployeeRegistrationDto.toEntity(): Employee =
    Employee(id, firstName!!, lastName!!, email!!, position)

fun EmployeeDto.toEmployeeRegistrationDto(): EmployeeRegistrationDto =
    EmployeeRegistrationDto(id, firstName!!, lastName!!, email!!, position)
