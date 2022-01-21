package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.CategoryDto
import com.ambrosia.nymph.services.CategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("business/{businessId}/category")
class CategoryController(@Autowired private val categoryService: CategoryService) {

	@PostMapping
	fun addCategory(
		@PathVariable("businessId") businessId: Long, @Valid @RequestBody category: CategoryDto
	): CategoryDto {
		return categoryService.addCategory(businessId, category)
	}

	@PutMapping("{categoryId}")
	fun editCategory(
		@PathVariable("businessId") businessId: Long,
		@PathVariable("categoryId") categoryId: Long,
		@Valid @RequestBody category: CategoryDto
	): CategoryDto {
		return categoryService.editCategory(businessId, categoryId, category)
	}

	@DeleteMapping("{categoryId}")
	fun deleteCategory(@PathVariable("businessId") businessId: Long, @PathVariable("categoryId") categoryId: Long) {
		return categoryService.deleteCategory(businessId, categoryId)
	}
}