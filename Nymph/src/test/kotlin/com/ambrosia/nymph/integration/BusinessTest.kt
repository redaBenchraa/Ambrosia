package com.ambrosia.nymph.integration

import com.ambrosia.nymph.constants.Role
import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.EmployeeRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestExecutionListeners(
    listeners = [DependencyInjectionTestExecutionListener::class, TransactionDbUnitTestExecutionListener::class]
)
@DatabaseSetup("classpath:business.xml")
@ActiveProfiles("test")
class BusinessTest {

    private val id: Long = 1000
    private val baseUrl = "/businesses"

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @Autowired
    private lateinit var businessRepository: BusinessRepository

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository


    @Test
    fun `Create a business with a manager`() {
        val content = objectMapper.writeValueAsString(getBusinessRegistrationDto())
        mockMvc
            .perform(post("$baseUrl/register").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))

        val businesses = businessRepository.findAll()
        assertEquals(2, businesses.size)
        assertNotNull(businesses[0].id)
        assertEquals("name", businesses[0].name)
        assertEquals("EUR", businesses[0].currency)
        assertEquals("desc", businesses[0].description)
        assertEquals("email2@email.com", businesses[0].email)
        assertEquals("phoneNumber", businesses[0].phoneNumber)
        assertEquals("location", businesses[0].location)
        assertEquals("slogan", businesses[0].slogan)
        assertEquals("logo", businesses[0].logo)

        val employees = employeeRepository.findByBusinessId(businesses[0].id!!)
        assertEquals(1, employees.size)
        assertNotNull(employees[0].id)
        assertEquals("firstName", employees[0].firstName)
        assertEquals("lastName", employees[0].lastName)
        assertEquals("email2@email.com", employees[0].email)
        assertEquals(Role.MANAGER, employees[0].position)
    }

    @Test
    fun `Create a business with an existing employee email`() {
        val content =
            objectMapper.writeValueAsString(getBusinessRegistrationDto().apply { employee?.email = "email@email.com" })
        val exception = EntityAlreadyExistsException(Employee::class.java, mutableMapOf("email" to "email@email.com"))
        val expected = runtimeExceptionHandler.handleEntityAlreadyExistsException(exception)
        mockMvc
            .perform(post("$baseUrl/register").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(CONFLICT.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))

        assertTrue(employeeRepository.existsByEmail("email@email.com"))

        val businesses = businessRepository.findAll()
        assertEquals(1, businesses.size)

        val employees = employeeRepository.findAll()
        assertEquals(1, employees.size)
    }

    @Test
    fun `Edit a business`() {
        val content = objectMapper.writeValueAsString(getBusinessRegistrationDto().copy(name = "new name"))
        mockMvc
            .perform(put("$baseUrl/$id").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))

        val business = businessRepository.findById(id)
        assertTrue(business.isPresent)
        assertEquals("new name", business.get().name)
    }

    @Test
    fun `Edit a non existing business`() {
        val content = objectMapper.writeValueAsString(getBusinessRegistrationDto())
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }


    private fun getBusinessRegistrationDto() =
        BusinessRegistrationDto(
            name = "name",
            currency = "EUR",
            description = "desc",
            email = "email2@email.com",
            phoneNumber = "phoneNumber",
            location = "location",
            logo = "logo",
            slogan = "slogan",
            employee = EmployeeRegistrationDto(
                firstName = "firstName",
                lastName = "lastName",
                password = "password",
                position = Role.MANAGER,
                email = "email2@email.com",
            ),
        )
}
