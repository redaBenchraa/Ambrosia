package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.entities.Category
import com.ambrosia.nymph.entities.Item
import com.ambrosia.nymph.entities.Menu
import com.ambrosia.nymph.entities.MenuItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MenuMapperTest {
    @Test
    fun `Get menu categories list`() {
        val item1 = Item(id = 1, name = "item1", price = 10.0)
        val item2 = Item(id = 2, name = "item2", price = 10.0)
        val item3 = Item(id = 3, name = "item3", price = 10.0)
        val item4 = Item(id = 4, name = "item4", price = 10.0)
        val category1 = Category(id = 5, name = "category1")
        val category2 = Category(id = 6, name = "category2")
        val category3 = Category(id = 7, name = "category3")
        val menu = Menu(
            id = 8,
            name = "name",
            price = 10.0,
            menuItems = mutableSetOf(
                MenuItem(id = 9, item = item1, category = category1, extra = 10.0),
                MenuItem(id = 10, item = item2, category = category2),
                MenuItem(id = 11, item = item3, category = category3),
                MenuItem(id = 12, item = item4, category = category3),
            )
        )
        val result = menu.toDto()
        assertEquals(3, result.categories.size)
        assertEquals(5, result.categories.first().category.id)
        assertEquals("category1", result.categories.first().category.name)
        assertEquals(1, result.categories.first().items.size)
        assertEquals(9, result.categories.first().items.first().id)
        assertEquals(10.0, result.categories.first().items.first().extra)
        assertEquals(1, result.categories.first().items.first().item.id)
        assertEquals(7, result.categories.last().category.id)
        assertEquals("category3", result.categories.last().category.name)
        assertEquals(2, result.categories.last().items.size)
        assertEquals(11, result.categories.last().items.first().id)
        assertEquals(3, result.categories.last().items.first().item.id)
        assertEquals(0.0, result.categories.last().items.first().extra)
        assertEquals(12, result.categories.last().items.last().id)
        assertEquals(4, result.categories.last().items.last().item.id)
    }
}