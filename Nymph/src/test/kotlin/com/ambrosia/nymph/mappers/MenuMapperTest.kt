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
        val item1 = Item(name = "item1", price = 10.0).apply { id = 1 }
        val item2 = Item(name = "item2", price = 10.0).apply { id = 2 }
        val item3 = Item(name = "item3", price = 10.0).apply { id = 3 }
        val item4 = Item(name = "item4", price = 10.0).apply { id = 4 }
        val category1 = Category(name = "category1").apply { id = 5 }
        val category2 = Category(name = "category2").apply { id = 6 }
        val category3 = Category(name = "category3").apply { id = 7 }
        val menu = Menu(
            name = "name",
            price = 10.0,
            menuItems = mutableSetOf(
                MenuItem(item = item1, category = category1, extra = 10.0).apply { id = 9 },
                MenuItem(item = item2, category = category2).apply { id = 10 },
                MenuItem(item = item3, category = category3).apply { id = 11 },
                MenuItem(item = item4, category = category3).apply { id = 12 },
            )
        ).apply { id = 8 }
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
