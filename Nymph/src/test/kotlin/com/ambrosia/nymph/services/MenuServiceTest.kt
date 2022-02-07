package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.AddMenuItemDto
import com.ambrosia.nymph.dtos.EditMenuItemDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Category
import com.ambrosia.nymph.entities.Item
import com.ambrosia.nymph.entities.Menu
import com.ambrosia.nymph.entities.MenuItem
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.CategoryRepository
import com.ambrosia.nymph.repositories.ItemRepository
import com.ambrosia.nymph.repositories.MenuItemRepository
import com.ambrosia.nymph.repositories.MenuRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class MenuServiceTest {

    private val businessRepository: BusinessRepository = mockk()
    private val menuRepository: MenuRepository = mockk()
    private val categoryRepository: CategoryRepository = mockk()
    private val itemRepository: ItemRepository = mockk()
    private val menuItemRepository: MenuItemRepository = mockk()
    private val menuService =
        MenuService(businessRepository, menuRepository, categoryRepository, itemRepository, menuItemRepository)

    @Test
    fun `Add a menu to a business`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { menuRepository.save(any()) } returns getMenu()
        menuService.addMenu(1, getMenu().toDto())
        verify {
            businessRepository.findById(any())
            menuRepository.save(any())
        }
    }

    @Test
    fun `Add menu to a non existing business`() {
        every { businessRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { menuService.addMenu(1, getMenu().toDto()) }
    }

    @Test
    fun `Edit a menu`() {
        every { businessRepository.existsById(any()) } returns true
        every { menuRepository.findById(any()) } returns Optional.of(getMenu())
        every { menuRepository.save(any()) } returns getMenu()
        val menuDto = getMenu().toDto().apply {
            name = "new name"
            description = "new description"
            image = "new image"
            price = 42.0
        }
        val result = menuService.editMenu(1, 4, menuDto)
        assertEquals("new name", result.name)
        assertEquals("new description", result.description)
        assertEquals("new image", result.image)
        assertEquals(42.0, result.price)
        verify {
            businessRepository.existsById(any())
            menuRepository.findById(any())
            menuRepository.save(any())
        }
    }

    @Test
    fun `Edit a menu with null values`() {
        every { businessRepository.existsById(any()) } returns true
        every { menuRepository.findById(any()) } returns Optional.of(getMenu())
        every { menuRepository.save(any()) } returns getMenu()
        val menuDto = getMenu().toDto().apply {
            name = null
            description = null
            image = null
            price = null
        }
        val result = menuService.editMenu(1, 4, menuDto)
        assertEquals("name", result.name)
        assertEquals("description", result.description)
        assertEquals("image", result.image)
        assertEquals(10.0, result.price)
        verify {
            businessRepository.existsById(any())
            menuRepository.findById(any())
            menuRepository.save(any())
        }
    }


    @Test
    fun `Edit a menu from a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { menuService.editMenu(1, 1, getMenu().toDto()) }
    }

    @Test
    fun `Edit a non existing menu`() {
        every { businessRepository.existsById(any()) } returns true
        every { menuRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { menuService.editMenu(1, 1, getMenu().toDto()) }
    }

    @Test
    fun `Remove a menu`() {
        every { businessRepository.existsById(any()) } returns true
        every { menuRepository.findById(any()) } returns Optional.of(getMenu())
        every { menuRepository.delete(any()) } returns Unit
        menuService.deleteMenu(1, 1)
        verify { menuRepository.delete(any()) }
    }

    @Test
    fun `Remove a menu from a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { menuService.deleteMenu(1, 1) }
    }

    @Test
    fun `Add an item to a menu`() {
        every { businessRepository.existsById(any()) } returns true
        every { menuRepository.findById(any()) } returns Optional.of(getMenu())
        every { categoryRepository.findById(any()) } returns Optional.of(getCategory())
        every { itemRepository.findById(any()) } returns Optional.of(getItem())
        every { menuRepository.save(any()) } returns getMenu()
        menuService.addItemToMenu(1, 1, AddMenuItemDto(itemId = 1, categoryId = 1, extra = 10.0))
        verify { menuRepository.save(any()) }
    }

    @Test
    fun `Add an item to a menu to a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> {
            menuService.addItemToMenu(1, 1, AddMenuItemDto(itemId = 1, categoryId = 1, extra = 10.0))
        }
    }

    @Test
    fun `Add an item to a menu to a non existing menu`() {
        every { businessRepository.existsById(any()) } returns true
        every { menuRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> {
            menuService.addItemToMenu(1, 1, AddMenuItemDto(itemId = 1, categoryId = 1, extra = 10.0))
        }
    }

    @Test
    fun `Add an item to a menu to a non existing category`() {
        every { businessRepository.existsById(any()) } returns true
        every { menuRepository.findById(any()) } returns Optional.of(getMenu())
        every { categoryRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> {
            menuService.addItemToMenu(1, 1, AddMenuItemDto(itemId = 1, categoryId = 1, extra = 10.0))
        }
    }

    @Test
    fun `Add a non existing item to a menu`() {
        every { businessRepository.existsById(any()) } returns true
        every { menuRepository.findById(any()) } returns Optional.of(getMenu())
        every { categoryRepository.findById(any()) } returns Optional.of(getCategory())
        every { itemRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> {
            menuService.addItemToMenu(1, 1, AddMenuItemDto(itemId = 1, categoryId = 1, extra = 10.0))
        }
    }

    @Test
    fun `Remove an item from a menu`() {
        every { businessRepository.existsById(any()) } returns true
        every { menuRepository.findById(any()) } returns Optional.of(getMenu())
        every { menuItemRepository.findById(any()) } returns Optional.of(getMenuItem())
        every { menuItemRepository.delete(any()) } returns Unit
        menuService.deleteMenuItem(1, 1, 1)
        verify { menuItemRepository.delete(any()) }
    }

    @Test
    fun `Remove an item to a menu from a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { menuService.deleteMenuItem(1, 1, 1) }
    }

    @Test
    fun `Remove an item to a menu from a non existing menu`() {
        every { businessRepository.existsById(any()) } returns true
        every { menuRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { menuService.deleteMenuItem(1, 1, 1) }
    }


    @Test
    fun `Remove a non existing item from a menu`() {
        every { businessRepository.existsById(any()) } returns true
        every { menuRepository.findById(any()) } returns Optional.of(getMenu())
        every { menuItemRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { menuService.deleteMenuItem(1, 1, 1) }
    }

    @Test
    fun `Edit extra for a menu`() {
        every { businessRepository.existsById(any()) } returns true
        every { menuRepository.findById(any()) } returns Optional.of(getMenu())
        every { menuItemRepository.findById(any()) } returns Optional.of(getMenuItem())
        every { menuItemRepository.save(any()) } returns getMenuItem()
        menuService.editMenuItemExtra(1, 1, 1, EditMenuItemDto(extra = 10.0))
        verify { menuItemRepository.save(any()) }
    }

    @Test
    fun `Edit extra for a menu with a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { menuService.editMenuItemExtra(1, 1, 1, EditMenuItemDto(extra = 10.0)) }
    }

    @Test
    fun `Edit extra for a non existing menu`() {
        every { businessRepository.existsById(any()) } returns true
        every { menuRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { menuService.editMenuItemExtra(1, 1, 1, EditMenuItemDto(extra = 10.0)) }
    }

    @Test
    fun `Edit extra for a non existing item`() {
        every { businessRepository.existsById(any()) } returns true
        every { menuRepository.findById(any()) } returns Optional.of(getMenu())
        every { menuItemRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { menuService.editMenuItemExtra(1, 1, 1, EditMenuItemDto(extra = 10.0)) }
    }

    @Test
    fun `Get business menus`() {
        every { businessRepository.existsById(any()) } returns true
        every { menuRepository.findByBusinessId(any()) } returns mutableListOf(getMenu())
        val result = menuService.getMenus(1)
        Assertions.assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(getMenu().toDto(), result[0])
    }

    @Test
    fun `Get items from a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { menuService.getMenus(1) }
    }

    private fun getMenuItem(): MenuItem =
        MenuItem(extra = 10.0, menu = getMenu(), category = getCategory(), item = getItem())

    private fun getItem(): Item = Item(name = "name", price = 10.0, business = getBusiness())

    private fun getCategory(): Category = Category(name = "name", business = getBusiness())

    private fun getMenu(): Menu =
        Menu(name = "name", price = 10.0, image = "image", description = "description", business = getBusiness())

    private fun getBusiness(): Business =
        Business(name = "name", email = "email", phoneNumber = "phoneNumber")

}
