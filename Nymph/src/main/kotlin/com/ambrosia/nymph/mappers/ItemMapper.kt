package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.constants.UNDEFINED_VALUE
import com.ambrosia.nymph.dtos.ItemDto
import com.ambrosia.nymph.entities.Item

fun Item.toDto(): ItemDto = ItemDto(id, name, description, image, price, onlyForMenu, deleted)

fun ItemDto.toEntity(): Item =
    Item(name ?: UNDEFINED_VALUE, description, image, price ?: 0.0, onlyForMenu ?: false).apply {
        id
        deleted
    }
