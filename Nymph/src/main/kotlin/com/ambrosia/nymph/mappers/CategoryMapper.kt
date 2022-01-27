package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.CategoryDto
import com.ambrosia.nymph.entities.Category

fun Category.toDto(): CategoryDto = CategoryDto(id, name, description, image, deleted)

fun CategoryDto.toEntity(): Category = Category(name!!, description, image)
    .apply {
        id
        deleted
    }
