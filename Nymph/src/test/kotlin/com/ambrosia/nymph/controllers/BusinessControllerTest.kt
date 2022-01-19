package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.constants.Role
import com.ambrosia.nymph.constants.Urls
import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Category
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toRegistrationEmployeeDto
import com.ambrosia.nymph.services.BusinessService
import com.ambrosia.nymph.services.CategoryService
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
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BusinessControllerTest {

	val baseUrl = "/business"

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

	@MockkBean
	private lateinit var employeeService: EmployeeService

	@MockkBean
	private lateinit var categoryService: CategoryService

	@Test
	fun `Create a new business with a manager`() {
		every { businessService.createBusiness(any()) } returns getBusinessRegistrationDto()
		val content = objectMapper.writeValueAsString(getBusinessRegistrationDto())
		mockMvc.perform(post("$baseUrl/register").contentType(APPLICATION_JSON).content(content))
			.andExpect(status().isOk)
			.andExpect(content().contentType(APPLICATION_JSON))
			.andExpect(content().json(objectMapper.writeValueAsString(getBusinessRegistrationDto())))
	}

	@Test
	fun `Register throws entity already exist exception`() {
		val exception = EntityAlreadyExistsException(Business::class.java, "email", "email@gmail.com")
		val expected = runtimeExceptionHandler.handleEntityAlreadyExistsException(exception)
		every { businessService.createBusiness(any()) } throws exception
		val content = objectMapper.writeValueAsString(getBusinessRegistrationDto())
		mockMvc.perform(
			post("$baseUrl/register")
				.contentType(APPLICATION_JSON)
				.content(content)
		)
			.andExpect(status().`is`(CONFLICT.value()))
			.andExpect(content().contentType(APPLICATION_JSON))
			.andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
	}

	@Test
	fun `Create business with blank name`() {
		val invalidBusinessDto = getBusinessRegistrationDto().apply { name = "" }
		every { businessService.createBusiness(any()) } returns getBusinessRegistrationDto()
		mockMvc.perform(
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
					"$.violations[0].message", `is`(translator.toLocale("error.business.name.blank"))
				)
			)
	}

	@Test
	fun `Create business with invalid employee name`() {
		val invalidBusinessDto = getBusinessRegistrationDto()
		invalidBusinessDto.employee?.email = "email"
		every { businessService.createBusiness(any()) } returns getBusinessRegistrationDto()
		mockMvc.perform(
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
			.andExpect(jsonPath("$.violations[0].field", `is`("employee.email")))
			.andExpect(
				jsonPath(
					"$.violations[0].message", `is`(translator.toLocale("error.employee.email.format.invalid"))
				)
			)
	}

	@Test
	fun `Add an employee to a business`() {
		every { employeeService.addEmployee(any(), any()) } returns getEmployee().toDto()
		val content = objectMapper.writeValueAsString(
			getEmployee().toRegistrationEmployeeDto().apply { password = "password" }
		)
		mockMvc.perform(post("$baseUrl/1/employees").contentType(APPLICATION_JSON).content(content))
			.andExpect(status().isOk)
			.andExpect(content().contentType(APPLICATION_JSON))
			.andExpect(content().json(objectMapper.writeValueAsString(getEmployee().toDto())))
	}

	@Test
	fun `Add an employee from a non existing business`() {
		val exception = EntityAlreadyExistsException(Business::class.java, "id", "1")
		val expected = runtimeExceptionHandler.handleEntityAlreadyExistsException(exception)
		val content = objectMapper.writeValueAsString(getEmployee().toRegistrationEmployeeDto().apply {
			password = "password"
		})
		every { employeeService.addEmployee(any(), any()) } throws exception
		mockMvc.perform(post("$baseUrl/1/employees").contentType(APPLICATION_JSON).content(content))
			.andExpect(status().`is`(CONFLICT.value()))
			.andExpect(content().contentType(APPLICATION_JSON))
			.andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
	}

	@Test
	fun `Add an employee with a blank password to a business`() {
		every { employeeService.addEmployee(any(), any()) } returns getEmployee().toDto()
		val content = objectMapper.writeValueAsString(getEmployee().toRegistrationEmployeeDto().apply { password = "" })
		mockMvc.perform(
			post("$baseUrl/1/employees")
				.contentType(APPLICATION_JSON)
				.content(content)
		)
			.andExpect(status().isBadRequest)
			.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
			.andExpect(jsonPath("$.type", `is`<Any>(Urls.VIOLATIONS)))
			.andExpect(jsonPath("$.title", `is`("Constraint Violation")))
			.andExpect(jsonPath("$.status", `is`(400)))
			.andExpect(jsonPath("$.violations", hasSize<Any>(2)))
			.andExpect(jsonPath("$.violations[0].field", `is`("password")))
			.andExpect(jsonPath("$.violations[1].field", `is`("password")))
			.andExpect(
				jsonPath(
					"$.violations[0].message", `is`(translator.toLocale("error.employee.password.blank"))
				)
			)
			.andExpect(
				jsonPath(
					"$.violations[1].message", `is`(translator.toLocale("error.employee.password.size.invalid"))
				)
			)
	}

	@Test
	fun `Delete an employee from a business`() {
		every { employeeService.deleteEmployee(any(), any()) } returns Unit
		mockMvc.perform(delete("$baseUrl/1/employees/1").contentType(APPLICATION_JSON))
			.andExpect(status().isOk)
	}

	@Test
	fun `Delete an employee from a non existing business`() {
		val exception = EntityAlreadyExistsException(Business::class.java, "id", "1")
		val expected = runtimeExceptionHandler.handleEntityAlreadyExistsException(exception)
		every { employeeService.deleteEmployee(any(), any()) } throws exception
		mockMvc.perform(delete("$baseUrl/1/employees/1"))
			.andExpect(status().`is`(CONFLICT.value()))
			.andExpect(content().contentType(APPLICATION_JSON))
			.andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
	}

	@Test
	fun `Add a category to a business`() {
		every { categoryService.addCategory(any(), any()) } returns getCategory().toDto()
		val content = objectMapper.writeValueAsString(getCategory().toDto())
		mockMvc.perform(post("$baseUrl/1/categories").contentType(APPLICATION_JSON).content(content))
			.andExpect(status().isOk)
			.andExpect(content().contentType(APPLICATION_JSON))
			.andExpect(content().json(objectMapper.writeValueAsString(getCategory().toDto())))
	}

	@Test
	fun `Add category with empty name to a business`() {
		every { categoryService.addCategory(any(), any()) } returns getCategory().toDto()
		val content = objectMapper.writeValueAsString(getCategory().toDto().apply { name = "" })
		mockMvc.perform(
			post("$baseUrl/1/categories")
				.contentType(APPLICATION_JSON)
				.content(content)
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
					"$.violations[0].message", `is`(translator.toLocale("error.category.name.null"))
				)
			)
	}

	@Test
	fun `Delete a category from a business`() {
		every { categoryService.deleteCategory(any(), any()) } returns Unit
		mockMvc.perform(delete("$baseUrl/1/categories/1").contentType(APPLICATION_JSON))
			.andExpect(status().isOk)
	}

	@Test
	fun `Delete a category from a non existing business`() {
		val exception = EntityAlreadyExistsException(Business::class.java, "id", "1")
		val expected = runtimeExceptionHandler.handleEntityAlreadyExistsException(exception)
		every { categoryService.deleteCategory(any(), any()) } throws exception
		mockMvc.perform(delete("$baseUrl/1/categories/1"))
			.andExpect(status().`is`(CONFLICT.value()))
			.andExpect(content().contentType(APPLICATION_JSON))
			.andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
	}

	private fun getBusinessRegistrationDto() = BusinessRegistrationDto(
		name = "name",
		currency = "EUR",
		description = "desc",
		email = "email@email.com",
		phoneNumber = "phoneNumber",
		location = "location",
		logo = "logo",
		slogan = "slogan",
		employee = EmployeeRegistrationDto(
			firstName = "firstName",
			lastName = "lastName",
			password = "password",
			position = Role.MANAGER,
			email = "email@email.com",
		),
	)

	private fun getCategory() = Category(
		name = "name",
		description = "description",
		image = "image",
	)

	private fun getBusiness() = Business(
		id = 1,
		name = "name",
		currency = "EUR",
		description = "desc",
		email = "email@email.com",
		phoneNumber = "phoneNumber",
		location = "location",
		logo = "logo",
		slogan = "slogan",
	)

	private fun getEmployee() = Employee(
		id = 1,
		firstName = "firstName",
		lastName = "lastName",
		position = Role.MANAGER,
		email = "email@email.com",
	)
}