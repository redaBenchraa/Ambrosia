package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.constants.DEFAULT_DOUBLE_VALUE
import com.ambrosia.nymph.constants.UNDEFINED_VALUE
import com.ambrosia.nymph.dtos.ItemDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Item

fun Item.toDto(): ItemDto = ItemDto(id, name, description, image, price, onlyForMenu, deleted)

fun ItemDto.toEntity(business: Business): Item =
    Item(name ?: UNDEFINED_VALUE, description, image, price ?: DEFAULT_DOUBLE_VALUE, onlyForMenu ?: false, business)
        .apply {
            id
            deleted
        }
