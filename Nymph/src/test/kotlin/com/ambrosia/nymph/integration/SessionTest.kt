package com.ambrosia.nymph.integration

import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Session
import com.ambrosia.nymph.entities.Table
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.repositories.SessionRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestExecutionListeners(
    listeners = [DependencyInjectionTestExecutionListener::class, TransactionDbUnitTestExecutionListener::class]
)
@DatabaseSetup("classpath:business.xml")
class SessionTest {

    private final val id: Long = 1009
    private final val businessId: Long = 1000
    private final val tableId: Long = 1003
    private val baseUrl = "/businesses/$businessId/tables/$tableId/sessions"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var sessionRepository: SessionRepository

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @Test
    fun `Get current session`() {
        mockMvc
            .perform(get(baseUrl))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.id", `is`(1009)))
            .andExpect(jsonPath("$.isApproved", `is`(false)))
            .andExpect(jsonPath("$.isPaid", `is`(false)))
            .andExpect(jsonPath("$.isClosed", `is`(false)))
    }

    @Test
    fun `Create new session`() {
        mockMvc
            .perform(get("/businesses/$businessId/tables/1010/sessions"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.isApproved", `is`(false)))
            .andExpect(jsonPath("$.isPaid", `is`(false)))
            .andExpect(jsonPath("$.isClosed", `is`(false)))
        assertEquals(3, sessionRepository.count())
    }


    @Test
    fun `Get current session from a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(get("/businesses/1/tables/$tableId/sessions"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Get current session from a non existing table`() {
        val exception = EntityNotFoundException(Table::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(get("/businesses/$businessId/tables/1/sessions"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as opened`() {
        mockMvc
            .perform(put("$baseUrl/$id/mark-as-opened"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.isPaid", `is`(false)))
            .andExpect(jsonPath("$.isApproved", `is`(false)))
            .andExpect(jsonPath("$.isClosed", `is`(false)))
        val result = sessionRepository.findById(id)
        assertTrue(result.isPresent)
        assertFalse(result.get().closed)
    }

    @Test
    fun `Mark session as opened of a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/1/tables/$tableId/sessions/$id/mark-as-opened"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as opened of a non existing table`() {
        val exception = EntityNotFoundException(Table::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/$businessId/tables/1/sessions/$id/mark-as-opened"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as opened of a non existing session`() {
        val exception = EntityNotFoundException(Session::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/$businessId/tables/$tableId/sessions/1/mark-as-opened"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as closed`() {
        mockMvc
            .perform(put("$baseUrl/$id/mark-as-closed"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.isPaid", `is`(false)))
            .andExpect(jsonPath("$.isApproved", `is`(false)))
            .andExpect(jsonPath("$.isClosed", `is`(true)))
        val result = sessionRepository.findById(id)
        assertTrue(result.isPresent)
        assertTrue(result.get().closed)
    }

    @Test
    fun `Mark session as closed of a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/1/tables/$tableId/sessions/$id/mark-as-closed"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as closed of a non existing table`() {
        val exception = EntityNotFoundException(Table::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/$businessId/tables/1/sessions/$id/mark-as-closed"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as closed of a non existing session`() {
        val exception = EntityNotFoundException(Session::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/$businessId/tables/$tableId/sessions/1/mark-as-closed"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as paid`() {
        mockMvc
            .perform(put("$baseUrl/$id/mark-as-paid"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.isPaid", `is`(true)))
            .andExpect(jsonPath("$.isApproved", `is`(false)))
            .andExpect(jsonPath("$.isClosed", `is`(false)))
        val result = sessionRepository.findById(id)
        assertTrue(result.isPresent)
        assertTrue(result.get().paid)
    }

    @Test
    fun `Mark session as paid of a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/1/tables/$tableId/sessions/$id/mark-as-paid"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as paid of a non existing table`() {
        val exception = EntityNotFoundException(Table::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/$businessId/tables/1/sessions/$id/mark-as-paid"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as paid of a non existing session`() {
        val exception = EntityNotFoundException(Session::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/$businessId/tables/$tableId/sessions/1/mark-as-paid"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as unpaid`() {
        mockMvc
            .perform(put("$baseUrl/$id/mark-as-unpaid"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.isPaid", `is`(false)))
            .andExpect(jsonPath("$.isApproved", `is`(false)))
            .andExpect(jsonPath("$.isClosed", `is`(false)))
        val result = sessionRepository.findById(id)
        assertTrue(result.isPresent)
        assertFalse(result.get().paid)
    }

    @Test
    fun `Mark session as unpaid of a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/1/tables/$tableId/sessions/$id/mark-as-unpaid"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as unpaid of a non existing table`() {
        val exception = EntityNotFoundException(Table::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/$businessId/tables/1/sessions/$id/mark-as-unpaid"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as unpaid of a non existing session`() {
        val exception = EntityNotFoundException(Session::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/$businessId/tables/$tableId/sessions/1/mark-as-unpaid"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as approved`() {
        mockMvc
            .perform(put("$baseUrl/$id/mark-as-approved"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.isPaid", `is`(false)))
            .andExpect(jsonPath("$.isApproved", `is`(true)))
            .andExpect(jsonPath("$.isClosed", `is`(false)))
        val result = sessionRepository.findById(id)
        assertTrue(result.isPresent)
        assertTrue(result.get().approved)
    }

    @Test
    fun `Mark session as approved of a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/1/tables/$tableId/sessions/$id/mark-as-approved"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as approved of a non existing table`() {
        val exception = EntityNotFoundException(Table::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/$businessId/tables/1/sessions/$id/mark-as-approved"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as approved of a non existing session`() {
        val exception = EntityNotFoundException(Session::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/$businessId/tables/$tableId/sessions/1/mark-as-approved"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as unapproved`() {
        mockMvc
            .perform(put("$baseUrl/$id/mark-as-unapproved"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.isPaid", `is`(false)))
            .andExpect(jsonPath("$.isApproved", `is`(false)))
            .andExpect(jsonPath("$.isClosed", `is`(false)))
        val result = sessionRepository.findById(id)
        assertTrue(result.isPresent)
        assertFalse(result.get().approved)
    }

    @Test
    fun `Mark session as unapproved of a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/1/tables/$tableId/sessions/$id/mark-as-unapproved"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as unapproved of a non existing table`() {
        val exception = EntityNotFoundException(Table::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/$businessId/tables/1/sessions/$id/mark-as-unapproved"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Mark session as unapproved of a non existing session`() {
        val exception = EntityNotFoundException(Session::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(put("/businesses/$businessId/tables/$tableId/sessions/1/mark-as-unapproved"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }
}
