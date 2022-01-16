package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.constants.Role
import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.services.BusinessService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

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


	@MockkBean
	private lateinit var businessService: BusinessService

	@Test
	@Throws(Exception::class)
	fun create() {
		val businessRegistrationDto = getBusinessRegistrationDto()
		every { businessService.createBusiness(any()) } returns getBusinessRegistrationDto()
		mockMvc.perform(
			post(BASE_URL + "register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(businessRegistrationDto))
		)
			.andExpect(MockMvcResultMatchers.status().isOk)
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
		//.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(businessDto)))
	}

//	@Test
//	@Throws(Exception::class)
//	fun createException() {
//		val exception = EntityAlreadyExistsException(Business::class.java, "email", "email@gmail.com")
//		val expected: Unit = runtimeExceptionHandler.handleEntityAlreadyExistsException(exception)
//		Mockito.`when`(service.create(ArgumentMatchers.any())).thenThrow(exception)
//		mockMvc!!.perform(
//			post(BASE_URL + "register")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(objectMapper!!.writeValueAsString(businessRegistrationDto))
//		)
//			.andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.CONFLICT.value()))
//			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//			.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expected.getBody())))
//	}
//

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