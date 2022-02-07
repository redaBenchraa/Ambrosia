package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.AddOrderDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Order
import com.ambrosia.nymph.entities.Session
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.exceptions.SessionClosedException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.services.OrderService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    val baseUrl = "/businesses/1/tables/1/sessions/1/orders"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @MockkBean
    private lateinit var orderService: OrderService

    @Test
    fun `Create a new order`() {
        val order = getOrder().toDto()
        every { orderService.createOrder(any(), any(), any(), any()) } returns order
        mockMvc
            .perform(
                post(baseUrl).contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(AddOrderDto()))
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(order)))
    }

    @Test
    fun `Create a new order for a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { orderService.createOrder(any(), any(), any(), any()) } throws exception
        mockMvc
            .perform(
                post(baseUrl).contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(AddOrderDto()))
            )
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Create a new order with a closed session`() {
        val exception = SessionClosedException(mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleSessionClosedException(exception)
        every { orderService.createOrder(any(), any(), any(), any()) } throws exception
        mockMvc
            .perform(
                post(baseUrl).contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(AddOrderDto()))
            )
            .andExpect(status().`is`(CONFLICT.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Add items to an order`() {
        val order = getOrder().toDto()
        every { orderService.addItemsToOrder(any(), any(), any(), any(), any()) } returns order
        mockMvc
            .perform(
                post("$baseUrl/1").contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(AddOrderDto()))
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(order)))
    }

    @Test
    fun `Add items to an order for a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { orderService.addItemsToOrder(any(), any(), any(), any(), any()) } throws exception
        mockMvc
            .perform(
                post("$baseUrl/1").contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(AddOrderDto()))
            )
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Add items to an order with a closed session`() {
        val exception = SessionClosedException(mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleSessionClosedException(exception)
        every { orderService.addItemsToOrder(any(), any(), any(), any(), any()) } throws exception
        mockMvc
            .perform(
                post("$baseUrl/1").contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(AddOrderDto()))
            )
            .andExpect(status().`is`(CONFLICT.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }


    @Test
    fun `Delete item from an order`() {
        every { orderService.removeItemFromOrder(any(), any(), any(), any(), any()) } returns Unit
        mockMvc
            .perform(delete("$baseUrl/1/items/1"))
            .andExpect(status().isOk)
    }

    @Test
    fun `Delete item from an order with a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { orderService.removeItemFromOrder(any(), any(), any(), any(), any()) } throws exception
        mockMvc
            .perform(delete("$baseUrl/1/items/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete item from an order with a closed session`() {
        val exception = SessionClosedException(mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleSessionClosedException(exception)
        every { orderService.removeItemFromOrder(any(), any(), any(), any(), any()) } throws exception
        mockMvc
            .perform(delete("$baseUrl/1/items/1"))
            .andExpect(status().`is`(CONFLICT.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Update order status to confirmed`() {
        val order = getOrder().toDto()
        every { orderService.updateOrderStatus(any(), any(), any(), any(), any()) } returns order
        mockMvc
            .perform(put("$baseUrl/1/update-status/confirmed"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(order)))
    }

    @Test
    fun `Update order status to approved`() {
        val order = getOrder().toDto()
        every { orderService.updateOrderStatus(any(), any(), any(), any(), any()) } returns order
        mockMvc
            .perform(put("$baseUrl/1/update-status/approved"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(order)))
    }

    @Test
    fun `Update order status to rejected`() {
        val order = getOrder().toDto()
        every { orderService.updateOrderStatus(any(), any(), any(), any(), any()) } returns order
        mockMvc
            .perform(put("$baseUrl/1/update-status/rejected"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(order)))
    }

    @Test
    fun `Update order status to canceled`() {
        val order = getOrder().toDto()
        every { orderService.updateOrderStatus(any(), any(), any(), any(), any()) } returns order
        mockMvc
            .perform(put("$baseUrl/1/update-status/canceled"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(order)))
    }

    @Test
    fun `Update order status to delivered`() {
        val order = getOrder().toDto()
        every { orderService.updateOrderStatus(any(), any(), any(), any(), any()) } returns order
        mockMvc
            .perform(put("$baseUrl/1/update-status/delivered"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(order)))
    }

    @Test
    fun `Update order status to ongoing`() {
        val order = getOrder().toDto()
        every { orderService.updateOrderStatus(any(), any(), any(), any(), any()) } returns order
        mockMvc
            .perform(put("$baseUrl/1/update-status/ongoing"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(order)))
    }

    @Test
    fun `Update order status of a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { orderService.updateOrderStatus(any(), any(), any(), any(), any()) } throws exception
        mockMvc
            .perform(put("$baseUrl/1/update-status/confirmed"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Update order status of a closed session`() {
        val exception = SessionClosedException(mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleSessionClosedException(exception)
        every { orderService.updateOrderStatus(any(), any(), any(), any(), any()) } throws exception
        mockMvc
            .perform(put("$baseUrl/1/update-status/confirmed"))
            .andExpect(status().`is`(CONFLICT.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Update order with invalid status `() {
        val exception = mockk<MethodArgumentTypeMismatchException>()
        val expected = runtimeExceptionHandler.handleMethodArgumentTypeMismatchException(exception)
        mockMvc
            .perform(put("$baseUrl/1/update-status/invalid"))
            .andExpect(status().`is`(HttpStatus.BAD_REQUEST.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    private fun getOrder(): Order = Order(session = getSession())

    private fun getSession(): Session =
        Session(
            approved = true,
            closed = false,
            paid = false,
            business = getBusiness()
        )

    private fun getBusiness() = Business(name = "name", email = "email", phoneNumber = "phoneNumber")
}
