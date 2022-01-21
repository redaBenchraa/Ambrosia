package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.MenuDto
import com.ambrosia.nymph.entities.Menu

fun Menu.toDto(): MenuDto = MenuDto(id, name, description, image, price)

fun MenuDto.toEntity(): Menu = Menu(id, name!!, description, image, price!!)
