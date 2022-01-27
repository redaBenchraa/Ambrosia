package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.constants.Role
import com.ambrosia.nymph.constants.VIOLATIONS
import com.ambrosia.nymph.dtos.EditEmailDto
import com.ambrosia.nymph.dtos.EditPositionDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Category
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.exceptions.KeycloakException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toRegistrationEmployeeDto
import com.ambrosia.nymph.services.EmployeeService
import com.ambrosia.nymph.utils.Translator
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

    val baseUrl = "/businesses/1/employees"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var translator: Translator

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @MockkBean
    private lateinit var employeeService: EmployeeService

    @Test
    fun `Add an employee to a business`() {
        every { employeeService.addEmployee(any(), any()) } returns getEmployee().toDto()
        val content =
            objectMapper.writeValueAsString(
                getEmployee().toRegistrationEmployeeDto().apply { password = "password" }
            )
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(getEmployee().toDto())))
    }

    @Test
    fun `Add an employee with a blank password to a business`() {
        every { employeeService.addEmployee(any(), any()) } returns getEmployee().toDto()
        val content =
            objectMapper.writeValueAsString(
                getEmployee().toRegistrationEmployeeDto().apply { password = "" }
            )
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.type", `is`<Any>(VIOLATIONS)))
            .andExpect(jsonPath("$.title", `is`("Constraint Violation")))
            .andExpect(jsonPath("$.status", `is`(400)))
            .andExpect(jsonPath("$.violations", hasSize<Any>(2)))
            .andExpect(jsonPath("$.violations[0].field", `is`("password")))
            .andExpect(jsonPath("$.violations[1].field", `is`("password")))
            .andExpect(
                jsonPath(
                    "$.violations[0].message",
                    `is`(translator.toLocale("error.employee.password.blank"))
                )
            )
            .andExpect(
                jsonPath(
                    "$.violations[1].message",
                    `is`(translator.toLocale("error.employee.password.size.invalid"))
                )
            )
    }

    @Test
    fun `Edit an employee`() {
        val employee = getEmployee().toDto()
        every { employeeService.editEmployee(any(), any(), any()) } returns employee
        val content = objectMapper.writeValueAsString(employee)
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(employee)))
    }

    @Test
    fun `Edit an employee email`() {
        val employee = EditEmailDto(email = "email@email.com")
        every { employeeService.editEmployeeEmail(any(), any(), any()) } returns getEmployee().toDto()
        val content = objectMapper.writeValueAsString(employee)
        mockMvc
            .perform(put("$baseUrl/1/email").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(employee)))
    }

    @Test
    fun `Edit an employee position`() {
        val employee = EditPositionDto(position = Role.MANAGER)
        every { employeeService.editEmployeePosition(any(), any(), any()) } returns getEmployee().toDto()
        val content = objectMapper.writeValueAsString(employee)
        mockMvc
            .perform(put("$baseUrl/1/position").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(employee)))
    }

    @Test
    fun `Edit and employee position wth keycloak error `() {
        val exception = KeycloakException("error.keycloak.edit")
        val expected = runtimeExceptionHandler.handleKeycloakException(exception)
        every { employeeService.editEmployeePosition(any(), any(), any()) } throws exception
        val content = objectMapper.writeValueAsString(EditPositionDto(position = Role.MANAGER))
        mockMvc
            .perform(put("$baseUrl/1/position").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(INTERNAL_SERVER_ERROR.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Add an employee from a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content =
            objectMapper.writeValueAsString(
                getEmployee().toRegistrationEmployeeDto().apply { password = "password" }
            )
        every { employeeService.addEmployee(any(), any()) } throws exception
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Edit an employee from an non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { employeeService.editEmployee(any(), any(), any()) } throws exception
        val content = objectMapper.writeValueAsString(getEmployee().toDto())
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Edit a non existing employee`() {
        val exception = EntityNotFoundException(Employee::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { employeeService.editEmployee(any(), any(), any()) } throws exception
        val content = objectMapper.writeValueAsString(getEmployee().toDto())
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete an employee from a business`() {
        every { employeeService.deleteEmployee(any(), any()) } returns Unit
        mockMvc.perform(delete("$baseUrl/1").contentType(APPLICATION_JSON)).andExpect(status().isOk)
    }

    @Test
    fun `Delete an employee from a non existing business`() {
        val exception = EntityNotFoundException(Category::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { employeeService.deleteEmployee(any(), any()) } throws exception
        mockMvc
            .perform(delete("$baseUrl/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    private fun getEmployee() =
        Employee(
            firstName = "firstName",
            lastName = "lastName",
            position = Role.MANAGER,
            email = "email@email.com",
        )
}
