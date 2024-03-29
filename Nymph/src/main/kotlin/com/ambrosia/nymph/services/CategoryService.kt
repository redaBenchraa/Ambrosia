package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.CategoryDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Category
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toEntity
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.CategoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class CategoryService(
    @Autowired private val businessRepository: BusinessRepository,
    @Autowired private val categoryRepository: CategoryRepository,
) {

    @Transactional
    fun addCategory(businessId: Long, categoryDto: CategoryDto): CategoryDto {
        val business = businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val category = categoryDto.toEntity(business)
        return categoryRepository.save(category).toDto()
    }

    @Transactional
    fun editCategory(businessId: Long, categoryId: Long, categoryDto: CategoryDto): CategoryDto {
        if (!businessRepository.existsById(businessId)) {
            throw EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId))
        }
        val category = categoryRepository.findById(categoryId)
            .orElseThrow { EntityNotFoundException(Category::class.java, mutableMapOf("id" to categoryId)) }
        categoryDto.name?.let { category.name = it }
        categoryDto.description?.let { category.description = it }
        categoryDto.image?.let { category.image = it }
        categoryRepository.save(category)
        return category.toDto()
    }

    @Transactional
    fun deleteCategory(businessId: Long, categoryId: Long) {
        if (!businessRepository.existsById(businessId)) {
            throw EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId))
        }
        val category = categoryRepository.findById(categoryId)
            .orElseThrow { EntityNotFoundException(Category::class.java, mutableMapOf("id" to categoryId)) }
        categoryRepository.delete(category)
    }
}
