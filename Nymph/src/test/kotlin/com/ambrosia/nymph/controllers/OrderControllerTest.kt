package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.AddOrderDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Item
import com.ambrosia.nymph.entities.Order
import com.ambrosia.nymph.entities.OrderedItem
import com.ambrosia.nymph.entities.Session
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.exceptions.SessionClosedException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.services.OrderService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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
    fun `Get new order`() {
        val order = getOrder().toDto()
        every { orderService.createOrder(any(), any(), any(), any()) } returns order
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(AddOrderDto())))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(order)))
    }

    @Test
    fun `Get current session from a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { orderService.createOrder(any(), any(), any(), any()) } throws exception
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(AddOrderDto())))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Get current session for a closed session`() {
        val exception = SessionClosedException(mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        every { orderService.createOrder(any(), any(), any(), any()) } throws exception
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(AddOrderDto())))
            .andExpect(status().`is`(CONFLICT.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }


    private fun getOrder(): Order = Order(session = getSession())

    private fun getOrderedItem(): OrderedItem =
        OrderedItem(order = getOrder(), description = "description", name = "name", price = 10.0, item = getItem())

    private fun getSession(): Session =
        Session(approved = true,
            closed = false,
            paid = false,
            business = getBusiness())

    private fun getItem(): Item =
        Item(name = "name",
            description = "description",
            image = "image",
            price = 10.0,
            onlyForMenu = true,
            business = getBusiness())

    private fun getBusiness() = Business(name = "name", email = "email", phoneNumber = "phoneNumber")
}
