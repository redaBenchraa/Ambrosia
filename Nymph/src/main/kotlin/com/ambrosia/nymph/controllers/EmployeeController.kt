package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.services.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("business/{businessId}/employee")
class EmployeeController(@Autowired private val employeeService: EmployeeService) {

    @PostMapping
    fun addEmployee(
        @PathVariable("businessId") businessId: Long,
        @Valid @RequestBody employee: EmployeeRegistrationDto
    ): EmployeeDto {
        return employeeService.addEmployee(businessId, employee)
    }

    @PutMapping("{employeeId}")
    fun editEmployee(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("employeeId") employeeId: Long,
        @Valid @RequestBody employee: EmployeeDto
    ): EmployeeDto {
        return employeeService.editEmployee(businessId, employeeId, employee)
    }

    @DeleteMapping("{employeeId}")
    fun deleteEmployee(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("employeeId") employeeId: Long
    ) {
        employeeService.deleteEmployee(businessId, employeeId)
    }
}
