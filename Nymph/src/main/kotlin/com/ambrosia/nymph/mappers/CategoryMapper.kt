package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.constants.UNDEFINED_VALUE
import com.ambrosia.nymph.dtos.CategoryDto
import com.ambrosia.nymph.entities.Category

fun Category.toDto(): CategoryDto = CategoryDto(id, name, description, image, deleted)

fun CategoryDto.toEntity(): Category = Category(name ?: UNDEFINED_VALUE, description, image)
    .apply {
        id
        deleted
    }

fun defaultCategory() = CategoryDto(name = UNDEFINED_VALUE, id = -1)
