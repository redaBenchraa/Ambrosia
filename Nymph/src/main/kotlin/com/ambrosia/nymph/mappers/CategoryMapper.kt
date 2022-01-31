package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.constants.UNDEFINED_VALUE
import com.ambrosia.nymph.dtos.CategoryDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Category

fun Category.toDto(): CategoryDto = CategoryDto(id, name, description, image, deleted)

fun CategoryDto.toEntity(business: Business): Category =
    Category(name ?: UNDEFINED_VALUE, description, image, business)
        .apply {
            id
            deleted
        }
