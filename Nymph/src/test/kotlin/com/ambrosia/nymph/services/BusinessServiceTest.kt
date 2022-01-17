package com.ambrosia.nymph.services

import com.ambrosia.nymph.constants.Role
import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.EmployeeRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class BusinessServiceTest {

	private val businessRepository: BusinessRepository = mockk()
	private val employeeRepository: EmployeeRepository = mockk()

	private val businessService: BusinessService = BusinessService(businessRepository, employeeRepository)

	@Test
	fun `Register a business with an employee`() {
		every { businessRepository.save(any()) } returns getBusiness()
		every { employeeRepository.save(any()) } returns getEmployee()
		val result = businessService.createBusiness(getBusinessRegistrationDto())
		verify {
			businessRepository.save(any())
			employeeRepository.save(any())
		}
		assertEquals(1, result.id)
		assertNotNull(result.employee)
		assertEquals(1, result.employee?.id)
	}

	@Test
	fun `Add an employee to a business`() {
		every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
		every { employeeRepository.save(any()) } returns getEmployee()
		val result = businessService.addEmployee(1, getEmployeeDto())
		verify {
			businessRepository.findById(any())
			employeeRepository.save(any())
		}
		assertEquals(1, result.id)
	}

	@Test
	fun `Add employee to a non existing business`() {
		every { businessRepository.findById(any()) } returns Optional.empty()
		assertThrows<EntityNotFoundException> { businessService.addEmployee(1, getEmployeeDto()) }
	}

	@Test
	fun `Remove an employee`() {
		every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
		every { employeeRepository.findById(any()) } returns Optional.of(getEmployee())
		every { employeeRepository.delete(any()) } returns Unit
		businessService.deleteEmployee(1, getEmployeeDto())
		verify { employeeRepository.delete(any()) }
	}

	@Test
	fun `Remove an employee from a non existing business`() {
		every { businessRepository.findById(any()) } returns Optional.empty()
		assertThrows<EntityNotFoundException> { businessService.deleteEmployee(1, getEmployeeDto()) }
	}

	@Test
	fun `Remove an employee with a null id`() {
		every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
		assertThrows<EntityNotFoundException> { businessService.deleteEmployee(1, getEmployeeDto().copy(id = null)) }
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
			employee = getEmployeeDto()
		)
	}

	private fun getEmployeeDto(): EmployeeDto = EmployeeDto(
		firstName = "firstName",
		lastName = "lastName",
		password = "password",
		position = Role.MANAGER,
		id = 1,
		email = "email@email.com"
	)

	private fun getBusiness(): Business = Business(
		name = "name",
		currency = "EUR",
		description = "desc",
		email = "email",
		phoneNumber = "phoneNumber",
		location = "location",
		logo = "logo",
		slogan = "slogan",
		id = 1,
	)

	private fun getEmployee(): Employee = Employee(
		firstName = "firstName",
		lastName = "lastName",
		position = Role.MANAGER,
		id = 1,
		email = "email@email.com"
	)

}
