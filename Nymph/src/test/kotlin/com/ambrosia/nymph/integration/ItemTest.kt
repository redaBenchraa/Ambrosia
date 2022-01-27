package com.ambrosia.nymph.integration

import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Item
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.repositories.ItemRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import org.junit.jupiter.api.Assertions.assertEquals
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestExecutionListeners(
    listeners = [DependencyInjectionTestExecutionListener::class, TransactionDbUnitTestExecutionListener::class]
)
@DatabaseSetup("classpath:business.xml")
class ItemTest {

    private val id: Long = 1004
    private final val businessId: Long = 1000
    private val baseUrl = "/businesses/$businessId/items"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @Autowired
    private lateinit var itemRepository: ItemRepository


    @Test
    fun `Add a item to a business`() {
        val item = getItem().toDto()
        val content = objectMapper.writeValueAsString(item)
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
        val result = itemRepository.findByBusinessId(businessId)
        assertEquals(2, result.size)
        assertEquals("name", result[1].name)
        assertEquals(10.0, result[1].price)
    }

    @Test
    fun `Edit a item`() {
        val item = getItem().toDto().apply { name = "new name" }
        val content = objectMapper.writeValueAsString(item)
        mockMvc
            .perform(put("$baseUrl/$id").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
        val result = itemRepository.findById(id)
        assertEquals("new name", result.get().name)
    }

    @Test
    fun `Edit a non existing item`() {
        val exception = EntityNotFoundException(Item::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content = objectMapper.writeValueAsString(getItem().toDto())
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Edit a item from an non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content = objectMapper.writeValueAsString(getItem().toDto())
        mockMvc
            .perform(put("/businesses/1/items/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete a item from a business`() {
        mockMvc.perform(delete("$baseUrl/$id").contentType(APPLICATION_JSON)).andExpect(status().isOk)
        val result = itemRepository.findById(id)
        assertTrue(result.isEmpty)
    }

    @Test
    fun `Delete a non existing item`() {
        val exception = EntityNotFoundException(Item::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(delete("$baseUrl/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete a item from a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, mutableMapOf("id" to 1))
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(delete("/businesses/1/items/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    private fun getItem() = Item(name = "name", price = 10.0)
}
