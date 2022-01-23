package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.TableDto
import com.ambrosia.nymph.services.TableService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("businesses/{businessId}/tables")
class TableController(@Autowired private val tableService: TableService) {

    @PostMapping
    fun addTable(
        @PathVariable("businessId") businessId: Long,
        @Valid @RequestBody table: TableDto
    ): TableDto {
        return tableService.addTable(businessId, table)
    }

    @PutMapping("{tableId}")
    fun editTable(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("tableId") tableId: Long,
        @Valid @RequestBody table: TableDto
    ): TableDto {
        return tableService.editTable(businessId, tableId, table)
    }

    @DeleteMapping("{tableId}")
    fun deleteTable(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("tableId") tableId: Long
    ) {
        return tableService.deleteTable(businessId, tableId)
    }
}
