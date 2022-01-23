package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.constants.Urls
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Table
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.services.TableService
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TableControllerTest {

    val baseUrl = "/businesses/1/tables"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var translator: Translator

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @MockkBean
    private lateinit var tableService: TableService

    @Test
    fun `Add a table to a business`() {
        val table = getTable().toDto()
        every { tableService.addTable(any(), any()) } returns table
        val content = objectMapper.writeValueAsString(table)
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(table)))
    }

    @Test
    fun `Edit a table`() {
        val table = getTable().toDto()
        every { tableService.editTable(any(), any(), any()) } returns table
        val content = objectMapper.writeValueAsString(table)
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(table)))
    }

    @Test
    fun `Edit a non existing table`() {
        val exception = EntityNotFoundException(Table::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { tableService.editTable(any(), any(), any()) } throws exception
        val content = objectMapper.writeValueAsString(getTable().toDto())
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Edit a table from an non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { tableService.editTable(any(), any(), any()) } throws exception
        val content = objectMapper.writeValueAsString(getTable().toDto())
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Add table with empty name to a business`() {
        val table = getTable().toDto()
        every { tableService.addTable(any(), any()) } returns table
        val content = objectMapper.writeValueAsString(table.apply { number = null })
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.type", `is`<Any>(Urls.VIOLATIONS)))
            .andExpect(jsonPath("$.title", `is`("Constraint Violation")))
            .andExpect(jsonPath("$.status", `is`(400)))
            .andExpect(jsonPath("$.violations", hasSize<Any>(1)))
            .andExpect(jsonPath("$.violations[0].field", `is`("number")))
            .andExpect(
                jsonPath(
                    "$.violations[0].message",
                    `is`(translator.toLocale("error.table.number.null"))
                )
            )
    }

    @Test
    fun `Delete a table from a business`() {
        every { tableService.deleteTable(any(), any()) } returns Unit
        mockMvc.perform(delete("$baseUrl/1").contentType(APPLICATION_JSON)).andExpect(status().isOk)
    }

    @Test
    fun `Delete a non existing table`() {
        val exception = EntityNotFoundException(Table::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { tableService.deleteTable(any(), any()) } throws exception
        mockMvc
            .perform(delete("$baseUrl/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete a table from a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { tableService.deleteTable(any(), any()) } throws exception
        mockMvc
            .perform(delete("$baseUrl/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    private fun getTable() = Table(id = 1, number = 1, isAvailable = true)
}
