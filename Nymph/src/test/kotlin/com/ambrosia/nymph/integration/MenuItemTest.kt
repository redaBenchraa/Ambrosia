package com.ambrosia.nymph.integration

import com.ambrosia.nymph.dtos.AddMenuItemDto
import com.ambrosia.nymph.dtos.EditMenuItemDto
import com.ambrosia.nymph.entities.*
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.handlers.RuntimeExceptionHandler
import com.ambrosia.nymph.repositories.MenuItemRepository
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
class MenuItemTest {

    private val id: Long = 1006
    private final val businessId: Long = 1000
    private final val menuId: Long = 1005
    private final val categoryId: Long = 1002
    private final val itemId: Long = 1004
    private val baseUrl = "/businesses/$businessId/menus/$menuId/items"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var runtimeExceptionHandler: RuntimeExceptionHandler

    @Autowired
    private lateinit var menuItemRepository: MenuItemRepository

    @Test
    fun `Add an item to a menu`() {
        val content = objectMapper.writeValueAsString(getAddMenuItemDto())
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
        val result = menuItemRepository.findByMenuId(menuId)
        assertEquals(2, result.size)
        assertEquals(10.0, result[1].extra)
    }

    @Test
    fun `Add an item to a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content = objectMapper.writeValueAsString(getAddMenuItemDto())
        mockMvc
            .perform(post("/businesses/1/menus/1/items").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
        assertEquals(1, menuItemRepository.findByMenuId(menuId).size)
    }

    @Test
    fun `Add an item to a non existing menu`() {
        val exception = EntityNotFoundException(Menu::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content = objectMapper.writeValueAsString(getAddMenuItemDto())
        mockMvc
            .perform(post("/businesses/1000/menus/1/items").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
        assertEquals(1, menuItemRepository.findByMenuId(menuId).size)
    }

    @Test
    fun `Add a non existing item to a menu`() {
        val exception = EntityNotFoundException(Item::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content = objectMapper.writeValueAsString(getAddMenuItemDto().apply { itemId = 1 })
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
        assertEquals(1, menuItemRepository.findByMenuId(menuId).size)
    }

    @Test
    fun `Add an item with a non existing category`() {
        val exception = EntityNotFoundException(Category::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content = objectMapper.writeValueAsString(getAddMenuItemDto().apply { categoryId = 1 })
        mockMvc
            .perform(post(baseUrl).contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
        assertEquals(1, menuItemRepository.findByMenuId(menuId).size)
    }

    @Test
    fun `Edit a menu item extra `() {
        val content = objectMapper.writeValueAsString(getEditMenuItemDto())
        mockMvc
            .perform(put("$baseUrl/$id").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
        val result = menuItemRepository.findById(id)
        assertTrue(result.isPresent)
        assertEquals(getEditMenuItemDto().extra, result.get().extra)
    }

    @Test
    fun `Edit a menu item of a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content = objectMapper.writeValueAsString(getEditMenuItemDto())
        mockMvc
            .perform(put("/businesses/1/menus/1/items/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
        val result = menuItemRepository.findById(id)
        assertTrue(result.isPresent)
        assertEquals(10.0, result.get().extra)
    }

    @Test
    fun `Edit a menu item of a non existing menu`() {
        val exception = EntityNotFoundException(Menu::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content = objectMapper.writeValueAsString(getEditMenuItemDto())
        mockMvc
            .perform(put("/businesses/1000/menus/1/items/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
        val result = menuItemRepository.findById(id)
        assertTrue(result.isPresent)
        assertEquals(10.0, result.get().extra)
    }

    @Test
    fun `Edit a non existing menu item`() {
        val exception = EntityNotFoundException(MenuItem::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        val content = objectMapper.writeValueAsString(getEditMenuItemDto())
        mockMvc
            .perform(put("$baseUrl/1").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
        val result = menuItemRepository.findById(id)
        assertTrue(result.isPresent)
        assertEquals(10.0, result.get().extra)
    }

    @Test
    fun `Delete a menu item`() {
        mockMvc
            .perform(delete("$baseUrl/$id").contentType(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
        val result = menuItemRepository.findById(id)
        assertTrue(result.isEmpty)
    }

    @Test
    fun `Delete a menu item from a non existing business`() {
        val exception = EntityNotFoundException(Business::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(delete("/businesses/1/menus/1/items/1").contentType(APPLICATION_JSON))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
        assertTrue(menuItemRepository.findById(id).isPresent)
    }

    @Test
    fun `Delete a menu item from a non existing menu`() {
        val exception = EntityNotFoundException(Menu::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(delete("/businesses/1000/menus/1/items/1").contentType(APPLICATION_JSON))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
        assertTrue(menuItemRepository.findById(id).isPresent)
    }

    @Test
    fun `Delete a non existing menu item`() {
        val exception = EntityNotFoundException(MenuItem::class.java, "id", "1")
        val expected = runtimeExceptionHandler.handleEntityNotFoundException(exception)
        mockMvc
            .perform(delete("$baseUrl/1").contentType(APPLICATION_JSON))
            .andExpect(status().`is`(NOT_FOUND.value()))
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expected.body)))
        assertTrue(menuItemRepository.findById(id).isPresent)
    }

    private fun getAddMenuItemDto() = AddMenuItemDto(itemId = itemId, categoryId = categoryId, extra = 10.0)
    private fun getEditMenuItemDto() = EditMenuItemDto(id = id, extra = 42.0)
}
