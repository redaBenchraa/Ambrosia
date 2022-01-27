package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.constants.VIOLATIONS
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Item
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.services.ItemService
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ItemControllerTest {

    val baseUrl = "/businesses/1/items"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var translator: Translator

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @MockkBean
    private lateinit var itemService: ItemService

    @Test
    fun `Add an item to a business`() {
        val item = getItem().toDto()
        every { itemService.addItem(any(), any()) } returns item
        val content = objectMapper.writeValueAsString(item)
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(item)))
    }

    @Test
    fun `Add an item from a non existing business`() {
        val exception = EntityAlreadyExistsException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityAlreadyExistsException(exception)
        val content = objectMapper.writeValueAsString(getItem().toDto())
        every { itemService.addItem(any(), any()) } throws exception
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(CONFLICT.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Add an item with a blank name to a business`() {
        val item = getItem().toDto()
        every { itemService.addItem(any(), any()) } returns item
        val content = objectMapper.writeValueAsString(item.apply { name = "" })
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.type", `is`<Any>(VIOLATIONS)))
            .andExpect(jsonPath("$.title", `is`("Constraint Violation")))
            .andExpect(jsonPath("$.status", `is`(400)))
            .andExpect(jsonPath("$.violations", hasSize<Any>(1)))
            .andExpect(jsonPath("$.violations[0].field", `is`("name")))
            .andExpect(
                jsonPath(
                    "$.violations[0].message",
                    `is`(translator.toLocale("error.item.name.blank"))
                )
            )
    }

    @Test
    fun `Edit an item`() {
        val item = getItem().toDto()
        every { itemService.editItem(any(), any(), any()) } returns item
        val content = objectMapper.writeValueAsString(item)
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(item)))
    }

    @Test
    fun `Edit a non existing item`() {
        val exception = EntityNotFoundException(Item::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { itemService.editItem(any(), any(), any()) } throws exception
        val content = objectMapper.writeValueAsString(getItem().toDto())
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Edit an item from an non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { itemService.editItem(any(), any(), any()) } throws exception
        val content = objectMapper.writeValueAsString(getItem().toDto())
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete an item from a business`() {
        every { itemService.deleteItem(any(), any()) } returns Unit
        mockMvc.perform(delete("$baseUrl/1").contentType(APPLICATION_JSON)).andExpect(status().isOk)
    }

    @Test
    fun `Delete an item from a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { itemService.deleteItem(any(), any()) } throws exception
        mockMvc
            .perform(delete("$baseUrl/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete a non existing item`() {
        val exception = EntityNotFoundException(Item::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { itemService.deleteItem(any(), any()) } throws exception
        mockMvc
            .perform(delete("$baseUrl/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    private fun getItem() =
        Item(
            name = "name",
            description = "description",
            image = "image",
            price = 10.0,
        )
}
