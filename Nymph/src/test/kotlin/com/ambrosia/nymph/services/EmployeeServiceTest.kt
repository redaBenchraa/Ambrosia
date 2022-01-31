package com.ambrosia.nymph.services

import com.ambrosia.nymph.constants.Role.EMPLOYEE
import com.ambrosia.nymph.constants.Role.MANAGER
import com.ambrosia.nymph.dtos.EditEmailDto
import com.ambrosia.nymph.dtos.EditPositionDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Customer
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.exceptions.KeycloakException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.EmployeeRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class EmployeeServiceTest {

    private val businessRepository: BusinessRepository = mockk()
    private val employeeRepository: EmployeeRepository = mockk()
    private val userService: UserService = mockk()
    private val employeeService = EmployeeService(businessRepository, employeeRepository, userService)

    @Test
    fun `Add an employee to a business`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { userService.verifyThatEmailDoesNotExists(any()) } returns Unit
        every { employeeRepository.save(any()) } returns getEmployee()
        every { userService.createKeycloakUser(any()) } returns Unit
        employeeService.addEmployee(1, getEmployeeRegistrationDto())
        verify {
            businessRepository.findById(any())
            employeeRepository.save(any())
        }
    }

    @Test
    fun `Add an employee with keycloak exception`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { userService.verifyThatEmailDoesNotExists(any()) } returns Unit
        every { employeeRepository.save(any()) } returns getEmployee()
        every { userService.createKeycloakUser(any()) } throws KeycloakException(message = "error.keycloak.createUser")
        assertThrows<KeycloakException> {
            employeeService.addEmployee(1, getEmployeeRegistrationDto())
        }
    }

    @Test
    fun `Add an employee to a business with an existing email`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { userService.verifyThatEmailDoesNotExists(any()) } throws
                EntityAlreadyExistsException(Customer::class.java, mutableMapOf())
        assertThrows<EntityAlreadyExistsException> {
            employeeService.addEmployee(1, getEmployeeRegistrationDto())
        }
    }

    @Test
    fun `Add employee to a non existing business`() {
        every { businessRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> {
            employeeService.addEmployee(1, getEmployeeRegistrationDto())
        }
    }

    @Test
    fun `Edit an employee`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { employeeRepository.findById(any()) } returns Optional.of(getEmployee())
        every { employeeRepository.save(any()) } returns getEmployee()
        val employee = getEmployee().toDto().apply {
            firstName = "new firstName"
            lastName = "new lastName"
        }
        val result = employeeService.editEmployee(1, 1, employee)
        assertEquals("new firstName", result.firstName)
        assertEquals("new lastName", result.lastName)
        verify {
            businessRepository.findById(any())
            employeeRepository.findById(any())
            employeeRepository.save(any())
        }
    }

    @Test
    fun `Edit an employee with null values`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { employeeRepository.findById(any()) } returns Optional.of(getEmployee())
        every { employeeRepository.save(any()) } returns getEmployee()
        val employee = getEmployee().toDto().apply {
            firstName = null
            lastName = null
        }
        val result = employeeService.editEmployee(1, 1, employee)
        assertEquals("firstName", result.firstName)
        assertEquals("lastName", result.lastName)
        verify {
            businessRepository.findById(any())
            employeeRepository.findById(any())
            employeeRepository.save(any())
        }
    }

    @Test
    fun `Edit an employee email`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { employeeRepository.findById(any()) } returns Optional.of(getEmployee())
        every { userService.updateEmail(any()) } returns Unit
        every { employeeRepository.save(any()) } returns getEmployee()
        val result = employeeService.editEmployeeEmail(1, 1, EditEmailDto(email = "email2@email.com"))
        assertEquals("email2@email.com", result.email)
        verify {
            businessRepository.findById(any())
            employeeRepository.findById(any())
            userService.updateEmail(any())
            employeeRepository.save(any())
        }
    }

    @Test
    fun `Edit an employee position`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { employeeRepository.findById(any()) } returns Optional.of(getEmployee())
        every { userService.updateRoles(any()) } returns Unit
        every { employeeRepository.save(any()) } returns getEmployee()
        val result = employeeService.editEmployeePosition(1, 1, EditPositionDto(position = EMPLOYEE))
        assertEquals(EMPLOYEE, result.position)
        verify {
            businessRepository.findById(any())
            employeeRepository.findById(any())
            userService.updateRoles(any())
            employeeRepository.save(any())
        }
    }

    @Test
    fun `Edit an employee from a non existing business`() {
        every { businessRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> {
            employeeService.editEmployee(1, 1, getEmployee().toDto())
        }
    }

    @Test
    fun `Edit a non existing employee`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { employeeRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> {
            employeeService.editEmployee(1, 1, getEmployee().toDto())
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

    @Test
    fun `Get business employees`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { employeeRepository.findByBusinessId(any()) } returns mutableListOf(getEmployee())
        val result = employeeService.getEmployees(1)
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(getEmployee().toDto(), result[0])
    }

    @Test
    fun `Get employees from a non existing business`() {
        every { businessRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { employeeService.getEmployees(1) }
    }


    private fun getEmployeeRegistrationDto(): EmployeeRegistrationDto =
        EmployeeRegistrationDto(
            firstName = "firstName",
            lastName = "lastName",
            password = "password",
            email = "email@email.com",
            position = MANAGER,
        )

    private fun getBusiness(): Business =
        Business(name = "name", currency = "EUR", email = "email", phoneNumber = "phoneNumber")

    private fun getEmployee(): Employee =
        Employee(
            firstName = "firstName",
            lastName = "lastName",
            position = MANAGER,
            email = "email@email.com"
        )
}
