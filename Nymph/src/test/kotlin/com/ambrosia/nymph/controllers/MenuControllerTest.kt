package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.constants.Urls
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Menu
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.services.MenuService
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
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MenuControllerTest {

    val baseUrl = "/businesses/1/menus"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var translator: Translator

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @MockkBean
    private lateinit var menuService: MenuService

    @Test
    fun `Add an menu to a business`() {
        val menu = getMenu().toDto()
        every { menuService.addMenu(any(), any()) } returns menu
        val content = objectMapper.writeValueAsString(menu)
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(menu)))
    }

    @Test
    fun `Add an menu from a non existing business`() {
        val exception = EntityAlreadyExistsException(Business::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityAlreadyExistsException(exception)
        val content = objectMapper.writeValueAsString(getMenu().toDto())
        every { menuService.addMenu(any(), any()) } throws exception
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(CONFLICT.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Add an menu with a blank name to a business`() {
        val menu = getMenu().toDto()
        every { menuService.addMenu(any(), any()) } returns menu
        val content = objectMapper.writeValueAsString(menu.apply { name = "" })
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
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
                    `is`(translator.toLocale("error.menu.name.blank"))
                )
            )
    }

    @Test
    fun `Edit an menu`() {
        val menu = getMenu().toDto()
        every { menuService.editMenu(any(), any(), any()) } returns menu
        val content = objectMapper.writeValueAsString(menu)
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(menu)))
    }

    @Test
    fun `Edit a non existing menu`() {
        val exception = EntityNotFoundException(Menu::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { menuService.editMenu(any(), any(), any()) } throws exception
        val content = objectMapper.writeValueAsString(getMenu().toDto())
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Edit an menu from an non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { menuService.editMenu(any(), any(), any()) } throws exception
        val content = objectMapper.writeValueAsString(getMenu().toDto())
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete an menu from a business`() {
        every { menuService.deleteMenu(any(), any()) } returns Unit
        mockMvc.perform(delete("$baseUrl/1").contentType(APPLICATION_JSON)).andExpect(status().isOk)
    }

    @Test
    fun `Delete an menu from a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { menuService.deleteMenu(any(), any()) } throws exception
        mockMvc
            .perform(delete("$baseUrl/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete a non existing menu`() {
        val exception = EntityNotFoundException(Menu::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { menuService.deleteMenu(any(), any()) } throws exception
        mockMvc
            .perform(delete("$baseUrl/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    private fun getMenu() =
        Menu(
            name = "name",
            description = "description",
            image = "image",
            price = 10.0,
        )
}
