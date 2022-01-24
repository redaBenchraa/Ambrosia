package com.ambrosia.nymph.integration

import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Menu
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.repositories.MenuRepository
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestExecutionListeners(
    listeners = [DependencyInjectionTestExecutionListener::class, TransactionDbUnitTestExecutionListener::class]
)
@DatabaseSetup("classpath:business.xml")
class MenuTest {

    private val id: Long = 1005
    private final val businessId: Long = 1000
    private val baseUrl = "/businesses/$businessId/menus"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @Autowired
    private lateinit var menuRepository: MenuRepository


    @Test
    fun `Add a menu to a business`() {
        val menu = getMenu().toDto()
        val content = objectMapper.writeValueAsString(menu)
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
        val result = menuRepository.findByBusinessId(businessId)
        assertEquals(2, result.size)
        assertEquals("name", result[1].name)
        assertEquals(10.0, result[1].price)
    }

    @Test
    fun `Edit a menu`() {
        val menu = getMenu().toDto().apply { name = "new name" }
        val content = objectMapper.writeValueAsString(menu)
        mockMvc
            .perform(put("$baseUrl/$id").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
        val result = menuRepository.findById(id)
        assertEquals("new name", result.get().name)
    }

    @Test
    fun `Edit a non existing menu`() {
        val exception = EntityNotFoundException(Menu::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content = objectMapper.writeValueAsString(getMenu().toDto())
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Edit a menu from an non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content = objectMapper.writeValueAsString(getMenu().toDto())
        mockMvc
            .perform(put("/businesses/1/menus/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete a menu from a business`() {
        mockMvc.perform(delete("$baseUrl/$id").contentType(APPLICATION_JSON)).andExpect(status().isOk)
        val result = menuRepository.findById(id)
        assertTrue(result.isEmpty)
    }

    @Test
    fun `Delete a non existing menu`() {
        val exception = EntityNotFoundException(Menu::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(delete("$baseUrl/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete a menu from a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(delete("/businesses/1/menus/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    private fun getMenu() = Menu(name = "name", price = 10.0)
}
