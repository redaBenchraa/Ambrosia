package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.EditEmailDto
import com.ambrosia.nymph.dtos.EditPositionDto
import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.services.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("businesses/{businessId}/employees")
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

    @PutMapping("{employeeId}/email")
    fun editEmployeeEmail(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("employeeId") employeeId: Long,
        @Valid @RequestBody editEmailDto: EditEmailDto
    ): EmployeeDto {
        return employeeService.editEmployeeEmail(businessId, employeeId, editEmailDto)
    }

    @PutMapping("{employeeId}/position")
    fun editEmployeePosition(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("employeeId") employeeId: Long,
        @Valid @RequestBody editPositionDto: EditPositionDto
    ): EmployeeDto {
        return employeeService.editEmployeePosition(businessId, employeeId, editPositionDto)
    }

    @GetMapping
    fun getEmployees(@PathVariable("businessId") businessId: Long): List<EmployeeDto> {
        return employeeService.getEmployees(businessId)
    }
}
