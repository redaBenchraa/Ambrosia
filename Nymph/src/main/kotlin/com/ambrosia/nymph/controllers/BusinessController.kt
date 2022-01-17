package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.services.BusinessService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("business")
class BusinessController(@Autowired private val businessService: BusinessService) {

	@PostMapping("/register")
	fun createBusiness(@Valid @RequestBody business: BusinessRegistrationDto): BusinessRegistrationDto {
		return businessService.createBusiness(business)
	}

	@PostMapping("/{id}/employees")
	fun addEmployee(
		@PathVariable("id") businessId: Long,
		@Valid @RequestBody employee: EmployeeRegistrationDto
	): EmployeeDto {
		return businessService.addEmployee(businessId, employee)
	}

	@DeleteMapping("/{businessId}/employees/{employeeId}")
	fun deleteEmployee(@PathVariable("businessId") businessId: Long, @PathVariable("employeeId") employeeId: Long) {
		businessService.deleteEmployee(businessId, employeeId)
	}
}