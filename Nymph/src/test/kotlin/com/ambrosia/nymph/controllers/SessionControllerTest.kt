package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Session
import com.ambrosia.nymph.entities.Table
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.services.SessionService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SessionControllerTest {

    val baseUrl = "/businesses/1/tables/1/sessions"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @MockkBean
    private lateinit var sessionService: SessionService

    @Test
    fun `Get current session`() {
        val session = getSession().toDto()
        every { sessionService.getCurrentSession(any(), any()) } returns session
        mockMvc
            .perform(get(baseUrl))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(session)))
    }

    @Test
    fun `Get current session from a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { sessionService.getCurrentSession(any(), any()) } throws exception
        mockMvc
            .perform(get(baseUrl))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Get current session from a non existing table`() {
        val exception = EntityNotFoundException(Table::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { sessionService.getCurrentSession(any(), any()) } throws exception
        mockMvc
            .perform(get(baseUrl))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as closed`() {
        val session = getSession().toDto()
        every { sessionService.markAsClosed(any(), any(), any()) } returns session
        mockMvc
            .perform(put("$baseUrl/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(session)))
    }

    @Test
    fun `Mark session as closed of a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { sessionService.markAsClosed(any(), any(), any()) } throws exception
        mockMvc
            .perform(put("$baseUrl/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }


    private fun getSession() = Session(isPaid = false, isClosed = false, isApproved = true)
}
