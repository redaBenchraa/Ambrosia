package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.MenuCategory
import com.ambrosia.nymph.dtos.MenuDto
import com.ambrosia.nymph.dtos.MenuItemDto
import com.ambrosia.nymph.entities.Menu
import com.ambrosia.nymph.entities.MenuItem

fun Menu.toDto(): MenuDto = MenuDto(id, name, description, image, price, categories = getCategories(menuItems))

fun MenuDto.toEntity(): Menu = Menu(id, name!!, description, image, price!!)

fun getCategories(menuItems: MutableSet<MenuItem>): MutableSet<MenuCategory> {
    return menuItems
        .groupBy { it.category }
        .map { pair ->
            MenuCategory(pair.key!!.toDto(), pair.value.map { MenuItemDto(it.id ?: -1, it.item!!.toDto(), it.extra) })
        }.toMutableSet()
}
