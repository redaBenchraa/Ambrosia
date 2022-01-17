package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.CategoryDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Category
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.CategoryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class CategoryServiceTest {

	private val businessRepository: BusinessRepository = mockk()
	private val categoryRepository: CategoryRepository = mockk()

	private val categoryService = CategoryService(businessRepository, categoryRepository)


	@Test
	fun `Add an category to a business`() {
		every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
		every { categoryRepository.save(any()) } returns getCategory()
		val result = categoryService.addCategory(1, getCategoryDto())
		verify {
			businessRepository.findById(any())
			categoryRepository.save(any())
		}
		assertEquals(1, result.id)
	}

	@Test
	fun `Add category to a non existing business`() {
		every { businessRepository.findById(any()) } returns Optional.empty()
		assertThrows<EntityNotFoundException> { categoryService.addCategory(1, getCategoryDto()) }
	}

	@Test
	fun `Remove an category`() {
		every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
		every { categoryRepository.findById(any()) } returns Optional.of(getCategory())
		every { categoryRepository.delete(any()) } returns Unit
		categoryService.deleteCategory(1, 1)
		verify { categoryRepository.delete(any()) }
	}

	@Test
	fun `Remove an category from a non existing business`() {
		every { businessRepository.findById(any()) } returns Optional.empty()
		assertThrows<EntityNotFoundException> { categoryService.deleteCategory(1, 1) }
	}


	private fun getCategoryDto(): CategoryDto = CategoryDto(
		name = "name",
		description = "description",
		image = "image",
	)

	private fun getBusiness(): Business = Business(
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

	private fun getCategory(): Category = Category(
		id = 1,
		name = "name",
		description = "description",
		image = "image",
	)

}
