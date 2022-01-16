package com.ambrosia.nymph.services

import com.ambrosia.nymph.constants.Role
import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.EmployeeRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BusinessServiceTest {

	private val businessRepository: BusinessRepository = mockk()
	private val employeeRepository: EmployeeRepository = mockk()

	private val businessService: BusinessService = BusinessService(businessRepository, employeeRepository)

	@Test
	fun register() {
		every { businessRepository.save(any()) } returns getBusiness()
		every { employeeRepository.save(any()) } returns getEmployee()
		val result = businessService.createBusiness(getBusinessRegistrationDto())
		verify {
			businessRepository.save(any())
			employeeRepository.save(any())
		}
		assertEquals("1", result.id)
		assertEquals("1", result.employee?.id)
	}

	private fun getBusinessRegistrationDto(): BusinessRegistrationDto {
		return BusinessRegistrationDto(
			name = "name",
			currency = "EUR",
			description = "desc",
			email = "email",
			phoneNumber = "phoneNumber",
			location = "location",
			logo = "logo",
			slogan = "slogan",
			id = null,
			employee = EmployeeDto(
				firstName = "firstName",
				lastName = "lastName",
				password = "password",
				position = Role.MANAGER,
				id = null,
			),
		)
	}

	private fun getBusiness(): Business = Business(
		name = "name",
		currency = "EUR",
		description = "desc",
		email = "email",
		phoneNumber = "phoneNumber",
		location = "location",
		logo = "logo",
		slogan = "slogan",
		id = "1",
	)

	private fun getEmployee(): Employee = Employee(
		firstName = "firstName",
		lastName = "lastName",
		position = Role.MANAGER,
		id = "1",
	)

}
