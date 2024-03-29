package com.ambrosia.nymph.services

import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Category
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.CategoryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class CategoryServiceTest {

    private val businessRepository: BusinessRepository = mockk()
    private val categoryRepository: CategoryRepository = mockk()
    private val categoryService = CategoryService(businessRepository, categoryRepository)

    @Test
    fun `Add a category to a business`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { categoryRepository.save(any()) } returns getCategory()
        categoryService.addCategory(1, getCategory().toDto())
        verify {
            businessRepository.findById(any())
            categoryRepository.save(any())
        }
    }

    @Test
    fun `Add category to a non existing business`() {
        every { businessRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { categoryService.addCategory(1, getCategory().toDto()) }
    }

    @Test
    fun `Edit a category`() {
        every { businessRepository.existsById(any()) } returns true
        every { categoryRepository.findById(any()) } returns Optional.of(getCategory())
        every { categoryRepository.save(any()) } returns getCategory()
        val categoryDto = getCategory().toDto().apply {
            name = "new name"
            description = "new description"
            image = "new image"

        }
        val result = categoryService.editCategory(1, 4, categoryDto)
        assertEquals("new name", result.name)
        assertEquals("new description", result.description)
        assertEquals("new image", result.image)
        verify {
            businessRepository.existsById(any())
            categoryRepository.findById(any())
            categoryRepository.save(any())
        }
    }

    @Test
    fun `Edit a category with null data`() {
        every { businessRepository.existsById(any()) } returns true
        every { categoryRepository.findById(any()) } returns Optional.of(getCategory())
        every { categoryRepository.save(any()) } returns getCategory()
        val categoryDto = getCategory().toDto().apply {
            name = null
            description = null
            image = null
        }
        val result = categoryService.editCategory(1, 4, categoryDto)
        assertEquals("name", result.name)
        verify {
            businessRepository.existsById(any())
            categoryRepository.findById(any())
            categoryRepository.save(any())
        }
    }

    @Test
    fun `Edit a category from a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> {
            categoryService.editCategory(1, 1, getCategory().toDto())
        }
    }

    @Test
    fun `Edit a non existing category`() {
        every { businessRepository.existsById(any()) } returns true
        every { categoryRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> {
            categoryService.editCategory(1, 1, getCategory().toDto())
        }
    }

    @Test
    fun `Remove a category`() {
        every { businessRepository.existsById(any()) } returns true
        every { categoryRepository.findById(any()) } returns Optional.of(getCategory())
        every { categoryRepository.delete(any()) } returns Unit
        categoryService.deleteCategory(1, 1)
        verify { categoryRepository.delete(any()) }
    }

    @Test
    fun `Remove a category from a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { categoryService.deleteCategory(1, 1) }
    }

    private fun getBusiness(): Business =
        Business(name = "name", currency = "EUR", email = "email", phoneNumber = "phoneNumber")

    private fun getCategory(): Category =
        Category(
            name = "name",
            description = "description",
            image = "image",
            business = Business("name", "phone", "email")
        )
}
