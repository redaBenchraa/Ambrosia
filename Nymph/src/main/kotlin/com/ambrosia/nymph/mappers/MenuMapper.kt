package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.constants.DEFAULT_DOUBLE_VALUE
import com.ambrosia.nymph.constants.UNDEFINED_VALUE
import com.ambrosia.nymph.dtos.MenuCategory
import com.ambrosia.nymph.dtos.MenuDto
import com.ambrosia.nymph.dtos.MenuItemDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Menu
import com.ambrosia.nymph.entities.MenuItem

fun Menu.toDto(): MenuDto = MenuDto(id, name, description, image, price, categories = getCategories(menuItems))

fun MenuDto.toEntity(business: Business): Menu =
    Menu(name ?: UNDEFINED_VALUE, description, image, price ?: DEFAULT_DOUBLE_VALUE, business)

fun getCategories(menuItems: MutableSet<MenuItem>): MutableSet<MenuCategory> {
    return menuItems
        .groupBy { it.category }
        .map { pair ->
            MenuCategory(
                pair.key.toDto(),
                pair.value.map {
                    MenuItemDto(it.id ?: -1, it.item.toDto(), it.extra)
                }
            )
        }.toMutableSet()
}
