package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.constants.Role
import com.ambrosia.nymph.constants.Urls
import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.services.BusinessService
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
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BusinessControllerTest {

    val baseUrl = "/businesses"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var translator: Translator

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @MockkBean
    private lateinit var businessService: BusinessService

    @Test
    fun `Register a new business with a manager`() {
        every { businessService.createBusiness(any()) } returns getBusinessRegistrationDto()
        val content = objectMapper.writeValueAsString(getBusinessRegistrationDto())
        mockMvc
            .perform(post("$baseUrl/register").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(
                content().json(objectMapper.writeValueAsString(getBusinessRegistrationDto()))
            )
    }

    @Test
    fun `Register with an already existing employee email`() {
        val exception = EntityAlreadyExistsException(Business::class.java, "email", "email@gmail.com")
        val expected = runtimeExceptionHandler.handleEntityAlreadyExistsException(exception)
        every { businessService.createBusiness(any()) } throws exception
        val content = objectMapper.writeValueAsString(getBusinessRegistrationDto())
        mockMvc
            .perform(post("$baseUrl/register").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(CONFLICT.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Register a business with blank name`() {
        val invalidBusinessDto = getBusinessRegistrationDto().apply { name = "" }
        every { businessService.createBusiness(any()) } returns getBusinessRegistrationDto()
        mockMvc
            .perform(
                post("$baseUrl/register")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidBusinessDto))
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.type", `is`<Any>(Urls.VIOLATIONS)))
            .andExpect(jsonPath("$.title", `is`("Constraint Violation")))
            .andExpect(jsonPath("$.status", `is`(400)))
            .andExpect(jsonPath("$.violations", hasSize<Any>(1)))
            .andExpect(jsonPath("$.violations[0].field", `is`("name")))
            .andExpect(
                jsonPath(
                    "$.violations[0].message",
                    `is`(translator.toLocale("error.business.name.blank"))
                )
            )
    }

    @Test
    fun `Register a business with invalid employee name`() {
        val content = objectMapper.writeValueAsString(getBusinessRegistrationDto().copy(email = "email"))
        every { businessService.createBusiness(any()) } returns getBusinessRegistrationDto()
        mockMvc
            .perform(post("$baseUrl/register").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.type", `is`<Any>(Urls.VIOLATIONS)))
            .andExpect(jsonPath("$.title", `is`("Constraint Violation")))
            .andExpect(jsonPath("$.status", `is`(400)))
            .andExpect(jsonPath("$.violations", hasSize<Any>(1)))
            .andExpect(jsonPath("$.violations[0].field", `is`("employee.email")))
            .andExpect(
                jsonPath(
                    "$.violations[0].message",
                    `is`(translator.toLocale("error.employee.email.format.invalid"))
                )
            )
    }

    private fun getBusinessRegistrationDto() =
        BusinessRegistrationDto(
            name = "name",
            currency = "EUR",
            description = "desc",
            email = "email@email.com",
            phoneNumber = "phoneNumber",
            location = "location",
            logo = "logo",
            slogan = "slogan",
            employee =
            EmployeeRegistrationDto(
                firstName = "firstName",
                lastName = "lastName",
                password = "password",
                position = Role.MANAGER,
                email = "email@email.com",
            ),
        )
}
