package com.ambrosia.nymph.services

import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Item
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.ItemRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class ItemServiceTest {

    private val businessRepository: BusinessRepository = mockk()
    private val itemRepository: ItemRepository = mockk()
    private val itemService = ItemService(businessRepository, itemRepository)

    @Test
    fun `Add a item to a business`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { itemRepository.save(any()) } returns getItem()
        itemService.addItem(1, getItem().toDto())
        verify {
            businessRepository.findById(any())
            itemRepository.save(any())
        }
    }

    @Test
    fun `Add item to a non existing business`() {
        every { businessRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { itemService.addItem(1, getItem().toDto()) }
    }

    @Test
    fun `Edit a item`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { itemRepository.findById(any()) } returns Optional.of(getItem())
        every { itemRepository.save(any()) } returns getItem()
        val itemDto = getItem().toDto().apply {
            name = "new name"
            description = "new description"
            image = "new image"
            price = 42.0
            onlyForMenu = false
        }
        val result = itemService.editItem(1, 1, itemDto)
        assertEquals("new name", result.name)
        assertEquals("new description", result.description)
        assertEquals("new image", result.image)
        assertEquals(42.0, result.price)
        assertEquals(false, result.onlyForMenu)
        verify {
            businessRepository.findById(any())
            itemRepository.findById(any())
            itemRepository.save(any())
        }
    }

    @Test
    fun `Edit a item with null values`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { itemRepository.findById(any()) } returns Optional.of(getItem())
        every { itemRepository.save(any()) } returns getItem()
        val itemDto = getItem().toDto().apply {
            name = null
            description = null
            image = null
            price = null
            onlyForMenu = null
        }
        val result = itemService.editItem(1, 1, itemDto)
        assertEquals("name", result.name)
        assertEquals("description", result.description)
        assertEquals("image", result.image)
        assertEquals(10.0, result.price)
        assertEquals(true, result.onlyForMenu)
        verify {
            businessRepository.findById(any())
            itemRepository.findById(any())
            itemRepository.save(any())
        }
    }

    @Test
    fun `Edit a item from a non existing business`() {
        every { businessRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { itemService.editItem(1, 1, getItem().toDto()) }
    }

    @Test
    fun `Edit a non existing item`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { itemRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { itemService.editItem(1, 1, getItem().toDto()) }
    }

    @Test
    fun `Remove a item`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { itemRepository.findById(any()) } returns Optional.of(getItem())
        every { itemRepository.delete(any()) } returns Unit
        itemService.deleteItem(1, 1)
        verify { itemRepository.delete(any()) }
    }

    @Test
    fun `Remove a item from a non existing business`() {
        every { businessRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { itemService.deleteItem(1, 1) }
    }

    @Test
    fun `Get business items`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { itemRepository.findByBusinessId(any()) } returns mutableListOf(getItem())
        val result = itemService.getItems(1)
        Assertions.assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(getItem().toDto(), result[0])
    }

    @Test
    fun `Get items from a non existing business`() {
        every { businessRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { itemService.getItems(1) }
    }

    private fun getBusiness(): Business =
        Business(name = "name", currency = "EUR", email = "email", phoneNumber = "phoneNumber")

    private fun getItem(): Item =
        Item(name = "name", description = "description", image = "image", price = 10.0, onlyForMenu = true)
}
