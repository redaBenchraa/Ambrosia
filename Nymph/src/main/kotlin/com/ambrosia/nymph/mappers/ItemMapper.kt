package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.ItemDto
import com.ambrosia.nymph.entities.Item

fun Item.toDto(): ItemDto = ItemDto(
	id, name, description, image, price, onlyForMenu, deleted
)

fun ItemDto.toEntity(): Item = Item(
	id, name!!, description, image, price!!, onlyForMenu, deleted = deleted
)
