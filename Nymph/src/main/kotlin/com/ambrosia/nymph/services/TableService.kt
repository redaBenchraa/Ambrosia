package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.TableDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Table
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toEntity
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.TableRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class TableService(
    @Autowired private val businessRepository: BusinessRepository,
    @Autowired private val tableRepository: TableRepository,
) {

    @Transactional
    fun addTable(businessId: Long, tableDto: TableDto): TableDto {
        val business = businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val table = tableDto.toEntity(business)
        return tableRepository.save(table).toDto()
    }

    @Transactional
    fun editTable(businessId: Long, tableId: Long, tableDto: TableDto): TableDto {
        if (!businessRepository.existsById(businessId)) {
            throw EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId))
        }
        val table = tableRepository.findById(tableId)
            .orElseThrow { EntityNotFoundException(Table::class.java, mutableMapOf("id" to tableId)) }
        tableDto.number?.let { table.number = it }
        tableDto.isAvailable?.let { table.isAvailable = it }
        tableRepository.save(table)
        return table.toDto()
    }

    @Transactional
    fun deleteTable(businessId: Long, tableId: Long) {
        if (!businessRepository.existsById(businessId)) {
            throw EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId))
        }
        val table = tableRepository.findById(tableId)
            .orElseThrow { EntityNotFoundException(Table::class.java, mutableMapOf("id" to tableId)) }
        tableRepository.delete(table)
    }
}
