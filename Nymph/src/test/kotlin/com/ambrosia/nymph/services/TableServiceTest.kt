package com.ambrosia.nymph.services

import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Table
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.TableRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class TableServiceTest {

    private val businessRepository: BusinessRepository = mockk()
    private val tableRepository: TableRepository = mockk()
    private val tableService = TableService(businessRepository, tableRepository)

    @Test
    fun `Add a table to a business`() {
        every { businessRepository.findById(any()) } returns Optional.of(getBusiness())
        every { tableRepository.save(any()) } returns getTable()
        tableService.addTable(1, getTable().toDto())
        verify {
            businessRepository.findById(any())
            tableRepository.save(any())
        }
    }

    @Test
    fun `Add table to a non existing business`() {
        every { businessRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { tableService.addTable(1, getTable().toDto()) }
    }

    @Test
    fun `Edit a table`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.findById(any()) } returns Optional.of(getTable())
        every { tableRepository.save(any()) } returns getTable()
        val tableDto = getTable().toDto().apply {
            number = 42
            available = false
        }
        val result = tableService.editTable(1, 4, tableDto)
        assertEquals(42, result.number)
        assertEquals(false, result.available)
        verify {
            businessRepository.existsById(any())
            tableRepository.findById(any())
            tableRepository.save(any())
        }
    }

    @Test
    fun `Edit a table with null values`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.findById(any()) } returns Optional.of(getTable())
        every { tableRepository.save(any()) } returns getTable()
        val tableDto = getTable().toDto().apply {
            number = null
            available = null
        }
        val result = tableService.editTable(1, 4, tableDto)
        assertEquals(1, result.number)
        assertEquals(true, result.available)
        verify {
            businessRepository.existsById(any())
            tableRepository.findById(any())
            tableRepository.save(any())
        }
    }

    @Test
    fun `Edit a table from a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> {
            tableService.editTable(1, 1, getTable().toDto())
        }
    }

    @Test
    fun `Edit a non existing table`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> {
            tableService.editTable(1, 1, getTable().toDto())
        }
    }

    @Test
    fun `Remove a table`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.findById(any()) } returns Optional.of(getTable())
        every { tableRepository.delete(any()) } returns Unit
        tableService.deleteTable(1, 1)
        verify { tableRepository.delete(any()) }
    }

    @Test
    fun `Remove a table from a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { tableService.deleteTable(1, 1) }
    }

    private fun getTable(): Table = Table(number = 1, available = true, business = getBusiness())

    private fun getBusiness(): Business =
        Business(name = "name", currency = "EUR", email = "email", phoneNumber = "phoneNumber")
}
