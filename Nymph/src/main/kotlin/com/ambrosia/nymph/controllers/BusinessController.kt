package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.dtos.CategoryDto
import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.services.BusinessService
import com.ambrosia.nymph.services.CategoryService
import com.ambrosia.nymph.services.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("business")
class BusinessController(
	@Autowired private val businessService: BusinessService,
	@Autowired private val employeeService: EmployeeService,
	@Autowired private val categoryService: CategoryService
) {

	@PostMapping("/register")
	fun createBusiness(@Valid @RequestBody business: BusinessRegistrationDto): BusinessRegistrationDto {
		return businessService.createBusiness(business)
	}

	@PostMapping("/{id}/employees")
	fun addEmployee(
		@PathVariable("id") businessId: Long,
		@Valid @RequestBody employee: EmployeeRegistrationDto
	): EmployeeDto {
		return employeeService.addEmployee(businessId, employee)
	}

	@PutMapping("/{businessId}/employees/{employeeId}")
	fun editEmployee(
		@PathVariable("businessId") businessId: Long,
		@PathVariable("employeeId") employeeId: Long,
		@Valid @RequestBody employee: EmployeeRegistrationDto
	): EmployeeDto {
		return employeeService.editEmployee(businessId, employeeId, employee)
	}


	@DeleteMapping("/{businessId}/employees/{employeeId}")
	fun deleteEmployee(@PathVariable("businessId") businessId: Long, @PathVariable("employeeId") employeeId: Long) {
		employeeService.deleteEmployee(businessId, employeeId)
	}

	@PostMapping("/{id}/categories")
	fun addCategory(
		@PathVariable("id") businessId: Long, @Valid @RequestBody category: CategoryDto
	): CategoryDto {
		return categoryService.addCategory(businessId, category)
	}

	@PutMapping("/{businessId}/categories/{categoryId}")
	fun editCategory(
		@PathVariable("businessId") businessId: Long,
		@PathVariable("categoryId") categoryId: Long,
		@Valid @RequestBody category: CategoryDto
	): CategoryDto {
		return categoryService.editCategory(businessId, categoryId, category)
	}

	@DeleteMapping("/{businessId}/categories/{categoryId}")
	fun deleteCategory(@PathVariable("businessId") businessId: Long, @PathVariable("categoryId") categoryId: Long) {
		return categoryService.deleteCategory(businessId, categoryId)
	}

}