package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.constants.Role
import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.services.BusinessService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BusinessControllerTest {

	companion object {
		const val BASE_URL = "/business/"
	}

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var objectMapper: ObjectMapper

	@Autowired
	private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

	@MockkBean
	private lateinit var businessService: BusinessService

	@Test
	fun `Create a new business with a manager`() {
		val businessRegistrationDto = getBusinessRegistrationDto()
		every { businessService.createBusiness(any()) } returns getBusinessRegistrationDto()
		mockMvc.perform(
			post(BASE_URL + "register").contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(businessRegistrationDto))
		)
			.andExpect(status().isOk)
			.andExpect(content().contentType(APPLICATION_JSON))
			.andExpect(content().json(objectMapper.writeValueAsString(businessRegistrationDto)))
	}

	@Test
	fun `Register throws entity already exist exception`() {
		val businessRegistrationDto = getBusinessRegistrationDto()
		val exception = EntityAlreadyExistsException(Business::class.java, "email", "email@gmail.com")
		val expected = runtimeExceptionHandler.handleEntityAlreadyExistsException(exception)
		every { businessService.createBusiness(any()) } throws exception
		mockMvc.perform(
			post(BASE_URL + "register")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(businessRegistrationDto))
		)
			.andExpect(status().`is`(CONFLICT.value()))
			.andExpect(content().contentType(APPLICATION_JSON))
			.andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
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