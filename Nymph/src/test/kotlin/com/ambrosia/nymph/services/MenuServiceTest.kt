package com.ambrosia.nymph.services

import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Menu
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.CategoryRepository
import com.ambrosia.nymph.repositories.ItemRepository
import com.ambrosia.nymph.repositories.MenuRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class MenuServiceTest {

    private val businessRepository: BusinessRepository = mockk()
    private val menuRepository: MenuRepository = mockk()
    private val categoryRepository: CategoryRepository = mockk()
    private val itemRepository: ItemRepository = mockk()
    private val menuService = MenuService(businessRepository, menuRepository, categoryRepository, itemRepository)

    @Test
    fun `Add a menu to a business`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { menuRepository.save(any()) } returns getMenu()
        val result = menuService.addMenu(1, getMenu().toDto())
        verify {
            businessRepository.findById(any())
            menuRepository.save(any())
        }
        assertEquals(1, result.id)
    }

    @Test
    fun `Add menu to a non existing business`() {
        every { businessRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { menuService.addMenu(1, getMenu().toDto()) }
    }

    @Test
    fun `Edit a menu`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { menuRepository.findById(any()) } returns Optional.of(getMenu())
        every { menuRepository.save(any()) } returns getMenu()
        val menuDto = getMenu().toDto().copy(name = "new name")
        val result = menuService.editMenu(1, 4, menuDto)
        assertEquals("new name", result.name)
        verify {
            businessRepository.findById(any())
            menuRepository.findById(any())
            menuRepository.save(any())
        }
    }

    @Test
    fun `Edit a menu from a non existing business`() {
        every { businessRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { menuService.editMenu(1, 1, getMenu().toDto()) }
    }

    @Test
    fun `Edit a non existing menu`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { menuRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { menuService.editMenu(1, 1, getMenu().toDto()) }
    }

    @Test
    fun `Remove a menu`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { menuRepository.findById(any()) } returns Optional.of(getMenu())
        every { menuRepository.delete(any()) } returns Unit
        menuService.deleteMenu(1, 1)
        verify { menuRepository.delete(any()) }
    }

    @Test
    fun `Remove a menu from a non existing business`() {
        every { businessRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { menuService.deleteMenu(1, 1) }
    }

    private fun getBusiness(): Business =
        Business(
            name = "name",
            currency = "EUR",
            description = "desc",
            email = "email",
            phoneNumber = "phoneNumber",
            location = "location",
            logo = "logo",
            slogan = "slogan",
            id = 1,
        )

    private fun getMenu(): Menu =
        Menu(id = 1, name = "name", description = "description", image = "image", price = 10.0)
}
