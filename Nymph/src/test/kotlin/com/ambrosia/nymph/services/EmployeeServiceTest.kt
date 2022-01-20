package com.ambrosia.nymph.services

import com.ambrosia.nymph.constants.Role
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toRegistrationEmployeeDto
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.EmployeeRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class EmployeeServiceTest {

	private val businessRepository: BusinessRepository = mockk()
	private val employeeRepository: EmployeeRepository = mockk()

	private val employeeService = EmployeeService(businessRepository, employeeRepository)

	@Test
	fun `Add an employee to a business`() {
		every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
		every { employeeRepository.save(any()) } returns getEmployee()
		every { employeeRepository.findByEmail(any()) } returns Optional.empty()
		val result = employeeService.addEmployee(1, getEmployeeRegistrationDto())
		verify {
			businessRepository.findById(any())
			employeeRepository.save(any())
		}
		assertEquals(1, result.id)
	}

	@Test
	fun `Add an employee to a business with an existing email`() {
		every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
		every { employeeRepository.findByEmail(any()) } returns Optional.of(getEmployee())
		assertThrows<EntityAlreadyExistsException> { employeeService.addEmployee(1, getEmployeeRegistrationDto()) }
	}

	@Test
	fun `Add employee to a non existing business`() {
		every { businessRepository.findById(any()) } returns Optional.empty()
		assertThrows<EntityNotFoundException> { employeeService.addEmployee(1, getEmployeeRegistrationDto()) }
	}

	@Test
	fun `Edit an employee`() {
		every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
		every { employeeRepository.findById(any()) } returns Optional.of(getEmployee())
		every { employeeRepository.save(any()) } returns getEmployee()
		val employeeDto = getEmployee().toRegistrationEmployeeDto().apply { firstName = "new name" }
		val result = employeeService.editEmployee(1, 1, employeeDto)
		assertEquals("new name", result.firstName)
		verify {
			businessRepository.findById(any())
			employeeRepository.findById(any())
			employeeRepository.save(any())
		}
	}

	@Test
	fun `Edit an employee from a non existing business`() {
		every { businessRepository.findById(any()) } returns Optional.empty()
		assertThrows<EntityNotFoundException> {
			employeeService.editEmployee(1, 1, getEmployee().toRegistrationEmployeeDto())
		}
	}

	@Test
	fun `Edit a non existing employee`() {
		every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
		every { employeeRepository.findById(any()) } returns Optional.empty()
		assertThrows<EntityNotFoundException> {
			employeeService.editEmployee(1, 1, getEmployee().toRegistrationEmployeeDto())
		}
	}

	@Test
	fun `Remove an employee`() {
		every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
		every { employeeRepository.findById(any()) } returns Optional.of(getEmployee())
		every { employeeRepository.delete(any()) } returns Unit
		employeeService.deleteEmployee(1, 1)
		verify { employeeRepository.delete(any()) }
	}

	@Test
	fun `Remove an employee from a non existing business`() {
		every { businessRepository.findById(any()) } returns Optional.empty()
		assertThrows<EntityNotFoundException> { employeeService.deleteEmployee(1, 1) }
	}

	private fun getEmployeeRegistrationDto(): EmployeeRegistrationDto = EmployeeRegistrationDto(
		firstName = "firstName",
		lastName = "lastName",
		password = "password",
		email = "email@email.com",
		position = Role.MANAGER,
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
