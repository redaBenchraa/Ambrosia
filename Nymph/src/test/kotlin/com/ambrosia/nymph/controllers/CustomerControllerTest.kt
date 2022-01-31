package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.constants.VIOLATIONS
import com.ambrosia.nymph.dtos.EditEmailDto
import com.ambrosia.nymph.entities.Customer
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toRegistrationCustomerDto
import com.ambrosia.nymph.services.CustomerService
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
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    val baseUrl = "/customers"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var translator: Translator

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @MockkBean
    private lateinit var customerService: CustomerService

    @Test
    fun `Add an customer to a business`() {
        every { customerService.addCustomer(any()) } returns getCustomer().toDto()
        val content =
            objectMapper.writeValueAsString(
                getCustomer().toRegistrationCustomerDto().apply { password = "password" }
            )
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(getCustomer().toDto())))
    }

    @Test
    fun `Add an customer with an invalid size email`() {
        every { customerService.addCustomer(any()) } returns getCustomer().toDto()
        val content =
            objectMapper.writeValueAsString(
                getCustomer().toRegistrationCustomerDto().apply { email = "${"e".repeat(255)}@email.com" }
            )
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.type", `is`<Any>(VIOLATIONS)))
            .andExpect(jsonPath("$.title", `is`("Constraint Violation")))
            .andExpect(jsonPath("$.status", `is`(400)))
            .andExpect(jsonPath("$.violations", hasSize<Any>(1)))
            .andExpect(jsonPath("$.violations[0].field", `is`("email")))
            .andExpect(
                jsonPath(
                    "$.violations[0].message",
                    `is`(translator.toLocale("error.customer.email.size.invalid"))
                )
            )
    }

    @Test
    fun `Edit an customer`() {
        val customer = getCustomer().toDto()
        every { customerService.editCustomer(any(), any()) } returns customer
        val content = objectMapper.writeValueAsString(customer)
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(customer)))
    }

    @Test
    fun `Edit an customer email`() {
        val customer = EditEmailDto(email = "email@email.com")
        every { customerService.editCustomerEmail(any(), any()) } returns getCustomer().toDto()
        val content = objectMapper.writeValueAsString(customer)
        mockMvc
            .perform(put("$baseUrl/1/email").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(customer)))
    }

    @Test
    fun `Edit a non existing customer`() {
        val exception = EntityNotFoundException(Customer::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { customerService.editCustomer(any(), any()) } throws exception
        val content = objectMapper.writeValueAsString(getCustomer().toDto())
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete an customer from a business`() {
        every { customerService.deleteCustomer(any()) } returns Unit
        mockMvc.perform(delete("$baseUrl/1").contentType(APPLICATION_JSON)).andExpect(status().isOk)
    }

    private fun getCustomer() =
        Customer(
            firstName = "firstName",
            lastName = "lastName",
            dateOfBirth = LocalDate.now(),
            email = "email@email.com",
        )
}
