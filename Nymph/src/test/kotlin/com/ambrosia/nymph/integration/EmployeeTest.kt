package com.ambrosia.nymph.integration

import com.ambrosia.nymph.constants.Role
import com.ambrosia.nymph.dtos.EditEmailDto
import com.ambrosia.nymph.dtos.EditPositionDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toRegistrationEmployeeDto
import com.ambrosia.nymph.repositories.EmployeeRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestExecutionListeners(
    listeners = [DependencyInjectionTestExecutionListener::class, TransactionDbUnitTestExecutionListener::class]
)
@DatabaseSetup("classpath:business.xml")
class EmployeeTest {

    private val id: Long = 1001
    private final val businessId: Long = 1000
    private val baseUrl = "/businesses/$businessId/employees"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

    @Test
    fun `Add a employee to a business`() {
        val employee = getEmployee().toRegistrationEmployeeDto().apply { password = "password" }
        val content = objectMapper.writeValueAsString(employee)
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
        val result = employeeRepository.findByBusinessId(1000)
        assertEquals(2, result.size)
        assertEquals("firstName", result[1].firstName)
    }

    @Test
    fun `Edit a employee`() {
        val employee = getEmployee().toDto().apply { firstName = "new name" }
        val content = objectMapper.writeValueAsString(employee)
        mockMvc
            .perform(put("$baseUrl/$id").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
        val result = employeeRepository.findById(id)
        assertEquals("new name", result.get().firstName)
    }

    @Test
    fun `Edit a non existing employee`() {
        val exception = EntityNotFoundException(Employee::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content = objectMapper.writeValueAsString(getEmployee().toDto())
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Edit a employee from an non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content = objectMapper.writeValueAsString(getEmployee().toDto())
        mockMvc
            .perform(put("/businesses/1/employees/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete a employee from a business`() {
        mockMvc.perform(delete("$baseUrl/$id").contentType(APPLICATION_JSON)).andExpect(status().isOk)
        val result = employeeRepository.findById(id)
        assertTrue(result.isEmpty)
    }

    @Test
    fun `Delete a non existing employee`() {
        val exception = EntityNotFoundException(Employee::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(delete("$baseUrl/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete a employee from a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(delete("/businesses/1/employees/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Edit email`() {
        val employee = EditEmailDto(email = "email2@email.com")
        val content = objectMapper.writeValueAsString(employee)
        mockMvc
            .perform(put("$baseUrl/$id/email").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(employee)))
        val result = employeeRepository.findByBusinessId(1000)
        assertEquals("email2@email.com", result[0].email)
    }

    @Test
    fun `Edit an position`() {
        val employee = EditPositionDto(position = Role.ADMIN)
        val content = objectMapper.writeValueAsString(employee)
        mockMvc
            .perform(put("$baseUrl/$id/position").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(employee)))
        val result = employeeRepository.findByBusinessId(1000)
        assertEquals(Role.ADMIN, result[0].position)
    }

    @Test
    fun `Get employees`() {
        mockMvc
            .perform(get(baseUrl))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$[0].firstName", `is`("firstName")))
            .andExpect(jsonPath("$[0].lastName", `is`("lastName")))
            .andExpect(jsonPath("$[0].email", `is`("email@email.com")))
            .andExpect(jsonPath("$[0].position", `is`("ADMIN")))
    }

    @Test
    fun `Get employees from a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(get("/businesses/1/employees"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    private fun getEmployee(): Employee =
        Employee(firstName = "firstName", lastName = "lastName", position = Role.MANAGER, email = "email2@email.com")
}
