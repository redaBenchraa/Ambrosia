package com.ambrosia.nymph.integration

import com.ambrosia.nymph.constants.OrderStatus.APPROVED
import com.ambrosia.nymph.constants.OrderStatus.CANCELED
import com.ambrosia.nymph.constants.OrderStatus.CONFIRMED
import com.ambrosia.nymph.constants.OrderStatus.DELIVERED
import com.ambrosia.nymph.constants.OrderStatus.ONGOING
import com.ambrosia.nymph.constants.OrderStatus.REJECTED
import com.ambrosia.nymph.dtos.AddOrderDto
import com.ambrosia.nymph.dtos.ItemsToOrder
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Order
import com.ambrosia.nymph.entities.Session
import com.ambrosia.nymph.entities.Table
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.exceptions.SessionClosedException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.repositories.OrderItemRepository
import com.ambrosia.nymph.repositories.OrderRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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
class OrderTest {

    private final val id: Long = 1011
    private final val orderItemId: Long = 1012
    private final val businessId: Long = 1000
    private final val tableId: Long = 1003
    private final val sessionId: Long = 1009
    private final val closedSessionId: Long = 1008
    private final val itemId: Long = 1004
    private val baseUrl = "/businesses/$businessId/tables/$tableId/sessions/$sessionId/orders"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var orderItemRepository: OrderItemRepository

    @Test
    fun `Create new order`() {
        val content = objectMapper.writeValueAsString(AddOrderDto(items = mutableSetOf(ItemsToOrder(id = itemId))))
        mockMvc
            .perform(
                post(baseUrl).contentType(APPLICATION_JSON).content(content)
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.orderItems[0].name", `is`("name")))
        val result = orderRepository.findAll()
        assertNotNull(result)
        assertEquals(2, result.size)
    }

    @Test
    fun `Create a new order with a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(
                post("/businesses/1/tables/$tableId/sessions/$sessionId/orders").contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(AddOrderDto()))
            )
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Create a new order with a non existing table`() {
        val exception = EntityNotFoundException(Table::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(
                post("/businesses/$businessId/tables/1/sessions/$sessionId/orders").contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(AddOrderDto()))
            )
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Create a new order with a non existing session`() {
        val exception = EntityNotFoundException(Session::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(
                post("/businesses/$businessId/tables/$tableId/sessions/1/orders").contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(AddOrderDto()))
            )
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Create a new order with a closed session`() {
        val exception = SessionClosedException(mutableMapOf("id" to 1008))
        val expected = runtimeExceptionHandler.handleSessionClosedException(exception)
        mockMvc
            .perform(
                post("/businesses/$businessId/tables/$tableId/sessions/1008/orders").contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(AddOrderDto()))
            )
            .andExpect(status().`is`(CONFLICT.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Add items to order`() {
        val content = objectMapper.writeValueAsString(AddOrderDto(items = mutableSetOf(ItemsToOrder(id = itemId))))
        mockMvc
            .perform(
                post("$baseUrl/$id").contentType(APPLICATION_JSON).content(content)
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.orderItems[0].name", `is`("name")))
        val result = orderRepository.findAll()
        assertNotNull(result)
        assertEquals(1, result.size)
    }

    @Test
    fun `Add items to with a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(
                post("/businesses/1/tables/$tableId/sessions/$sessionId/orders/$id").contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(AddOrderDto()))
            )
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Add items to order with a non existing table`() {
        val exception = EntityNotFoundException(Table::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(
                post("/businesses/$businessId/tables/1/sessions/$sessionId/orders/$id").contentType(
                    APPLICATION_JSON
                )
                    .content(objectMapper.writeValueAsString(AddOrderDto()))
            )
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Add items to order with a non existing session`() {
        val exception = EntityNotFoundException(Session::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(
                post("/businesses/$businessId/tables/$tableId/sessions/1/orders/$id").contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(AddOrderDto()))
            )
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Add items to order with a closed session`() {
        val exception = SessionClosedException(mutableMapOf("id" to 1008))
        val expected = runtimeExceptionHandler.handleSessionClosedException(exception)
        mockMvc
            .perform(
                post("/businesses/$businessId/tables/$tableId/sessions/1008/orders/$id").contentType(
                    APPLICATION_JSON
                )
                    .content(objectMapper.writeValueAsString(AddOrderDto()))
            )
            .andExpect(status().`is`(CONFLICT.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Remove item to order`() {
        mockMvc.perform(delete("$baseUrl/$id/items/$orderItemId"))
            .andExpect(status().isOk)
        val result = orderItemRepository.findAll()
        assertNotNull(result)
        assertEquals(0, result.size)
    }

    @Test
    fun `Remove item with a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(delete("/businesses/1/tables/$tableId/sessions/$sessionId/orders/$id/items/$orderItemId"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Remove item with a non existing table`() {
        val exception = EntityNotFoundException(Table::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(delete("/businesses/$businessId/tables/1/sessions/$sessionId/orders/$id/items/$orderItemId"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Remove item with a non existing session`() {
        val exception = EntityNotFoundException(Session::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(delete("/businesses/$businessId/tables/$tableId/sessions/1/orders/$id/items/$orderItemId"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Remove item with a non existing order`() {
        val exception = EntityNotFoundException(Order::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(delete("$baseUrl/1/items/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Remove item with a closed session`() {
        val exception = SessionClosedException(mutableMapOf("id" to 1008))
        val expected = runtimeExceptionHandler.handleSessionClosedException(exception)
        mockMvc
            .perform(delete("/businesses/$businessId/tables/$tableId/sessions/1008/orders/$id/items/$orderItemId"))
            .andExpect(status().`is`(CONFLICT.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Update order status to confirmed`() {
        mockMvc
            .perform(put("$baseUrl/$id/update-status/confirmed").contentType(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.orderItems[0].name", `is`("name")))
        val result = orderRepository.findById(id)
        assertTrue(result.isPresent)
        assertEquals(CONFIRMED, result.get().status)
    }

    @Test
    fun `Update order status to approved`() {
        mockMvc.perform(put("$baseUrl/$id/update-status/confirmed").contentType(APPLICATION_JSON))
        mockMvc
            .perform(put("$baseUrl/$id/update-status/approved").contentType(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.orderItems[0].name", `is`("name")))
        val result = orderRepository.findById(id)
        assertTrue(result.isPresent)
        assertEquals(APPROVED, result.get().status)
    }

    @Test
    fun `Update order status to canceled`() {
        mockMvc
            .perform(put("$baseUrl/$id/update-status/canceled").contentType(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.orderItems[0].name", `is`("name")))
        val result = orderRepository.findById(id)
        assertTrue(result.isPresent)
        assertEquals(CANCELED, result.get().status)
    }

    @Test
    fun `Update order status to rejected`() {
        mockMvc.perform(put("$baseUrl/$id/update-status/confirmed").contentType(APPLICATION_JSON))
        mockMvc
            .perform(put("$baseUrl/$id/update-status/rejected").contentType(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.orderItems[0].name", `is`("name")))
        val result = orderRepository.findById(id)
        assertTrue(result.isPresent)
        assertEquals(REJECTED, result.get().status)
    }


    @Test
    fun `Update order status to ongoing`() {
        mockMvc.perform(put("$baseUrl/$id/update-status/confirmed").contentType(APPLICATION_JSON))
        mockMvc.perform(put("$baseUrl/$id/update-status/approved").contentType(APPLICATION_JSON))
        mockMvc
            .perform(put("$baseUrl/$id/update-status/ongoing").contentType(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.orderItems[0].name", `is`("name")))
        val result = orderRepository.findById(id)
        assertTrue(result.isPresent)
        assertEquals(ONGOING, result.get().status)
    }

    @Test
    fun `Update order status to delivered`() {
        mockMvc.perform(put("$baseUrl/$id/update-status/confirmed").contentType(APPLICATION_JSON))
        mockMvc.perform(put("$baseUrl/$id/update-status/approved").contentType(APPLICATION_JSON))
        mockMvc.perform(put("$baseUrl/$id/update-status/ongoing").contentType(APPLICATION_JSON))
        mockMvc
            .perform(put("$baseUrl/$id/update-status/delivered").contentType(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.orderItems[0].name", `is`("name")))
        val result = orderRepository.findById(id)
        assertTrue(result.isPresent)
        assertEquals(DELIVERED, result.get().status)
    }

    @Test
    fun `Update order status with a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(
                put("/businesses/1/tables/$tableId/sessions/$sessionId/orders/$id/update-status/confirmed")
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Update order status with a non existing table`() {
        val exception = EntityNotFoundException(Table::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(
                put("/businesses/$businessId/tables/1/sessions/$sessionId/orders/$id/update-status/confirmed")
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Update order status with a non existing session`() {
        val exception = EntityNotFoundException(Session::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(
                put("/businesses/$businessId/tables/$tableId/sessions/1/orders/$id/update-status/confirmed")
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Update order status with a closed session`() {
        val exception = SessionClosedException(mutableMapOf("id" to 1008))
        val expected = runtimeExceptionHandler.handleSessionClosedException(exception)
        mockMvc
            .perform(
                put("/businesses/$businessId/tables/$tableId/sessions/$closedSessionId/orders/$id/update-status/confirmed")
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().`is`(CONFLICT.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }
}
