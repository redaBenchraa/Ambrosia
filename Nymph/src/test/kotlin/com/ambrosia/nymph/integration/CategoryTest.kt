package com.ambrosia.nymph.integration

import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Category
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.repositories.CategoryRepository
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
class CategoryTest {

    private val id: Long = 1002
    private final val businessId: Long = 1000
    private val baseUrl = "/businesses/$businessId/categories"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Test
    fun `Add a category to a business`() {
        val category = getCategory().toDto()
        val content = objectMapper.writeValueAsString(category)
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
        val result = categoryRepository.findByBusinessId(1000)
        assertEquals(2, result.size)
        assertEquals("name", result[1].name)
    }

    @Test
    fun `Edit a category`() {
        val category = getCategory().toDto().apply { name = "new name" }
        val content = objectMapper.writeValueAsString(category)
        mockMvc
            .perform(put("$baseUrl/$id").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
        val result = categoryRepository.findById(id)
        assertEquals("new name", result.get().name)
    }

    @Test
    fun `Edit a non existing category`() {
        val exception = EntityNotFoundException(Category::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content = objectMapper.writeValueAsString(getCategory().toDto())
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Edit a category from an non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content = objectMapper.writeValueAsString(getCategory().toDto())
        mockMvc
            .perform(put("/businesses/1/categories/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete a category from a business`() {
        mockMvc.perform(delete("$baseUrl/$id").contentType(APPLICATION_JSON)).andExpect(status().isOk)
        val result = categoryRepository.findById(id)
        assertTrue(result.isEmpty)
    }

    @Test
    fun `Delete a non existing category`() {
        val exception = EntityNotFoundException(Category::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(delete("$baseUrl/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    @Test
    fun `Delete a category from a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(delete("/businesses/1/categories/1"))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
    }

    private fun getCategory() = Category(name = "name", description = "description", image = "image")
}
