package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.constants.UNDEFINED_VALUE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CategoryMapperTest {
    @Test
    fun `Default category`() {
        assertEquals(-1, defaultCategory().id)
        assertEquals(UNDEFINED_VALUE, defaultCategory().name)
    }
}
