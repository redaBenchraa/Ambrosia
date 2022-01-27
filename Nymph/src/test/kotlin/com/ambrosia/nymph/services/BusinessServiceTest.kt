package com.ambrosia.nymph.services

import com.ambrosia.nymph.constants.Role
import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.repositories.BusinessRepository
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
    private val employeeService: EmployeeService = mockk()
    private val businessService: BusinessService = BusinessService(businessRepository, employeeService)

    @Test
    fun `Register a business with an employee`() {
        every { businessRepository.save(any()) } returns getBusiness()
        every { employeeService.addEmployee(any(), any()) } returns getEmployee().toDto()
        val result = businessService.createBusiness(getBusinessRegistrationDto())
        verify {
            businessRepository.save(any())
            employeeService.addEmployee(any(), any())
        }
        assertEquals(1, result.id)
        assertNotNull(result.employee)
        assertEquals(1, result.employee?.id)
    }

    @Test
    fun `Edit business`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { businessRepository.save(any()) } returns getBusiness()
        val result = businessService.editBusiness(1, getBusinessRegistrationDto().copy(name = "new name"))
        assertEquals("new name", result.name)
        verify {
            businessRepository.findById(any())
            businessRepository.save(any())
        }
    }

    @Test
    fun `Edit a non existing business`() {
        every { businessRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> {
            businessService.editBusiness(1, getBusinessRegistrationDto())
        }
    }

    private fun getBusinessRegistrationDto(): BusinessRegistrationDto {
        return BusinessRegistrationDto(
            id = 1,
            name = "name",
            currency = "EUR",
            description = "desc",
            email = "email",
            phoneNumber = "phoneNumber",
            location = "location",
            logo = "logo",
            slogan = "slogan",
            employee = getEmployeeRegistrationDto()
        )
    }

    private fun getEmployeeRegistrationDto(): EmployeeRegistrationDto =
        EmployeeRegistrationDto(
            firstName = "firstName",
            lastName = "lastName",
            password = "password",
            email = "email@field:Email.com",
            position = Role.MANAGER,
        )

    private fun getBusiness(): Business =
        Business(name = "name", currency = "EUR", email = "email", phoneNumber = "phoneNumber").apply { id = 1 }

    private fun getEmployee(): Employee =
        Employee(
            firstName = "firstName",
            lastName = "lastName",
            position = Role.MANAGER,
            email = "email@email.com"
        ).apply { id = 1 }
}
