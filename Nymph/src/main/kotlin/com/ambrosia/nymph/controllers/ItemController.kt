package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.ItemDto
import com.ambrosia.nymph.services.ItemService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("business/{businessId}/item")
class ItemController(@Autowired private val itemService: ItemService) {

    @PostMapping
    fun addItems(
        @PathVariable("businessId") businessId: Long,
        @Valid @RequestBody item: ItemDto
    ): ItemDto {
        return itemService.addItem(businessId, item)
    }

    @PutMapping("{itemId}")
    fun editItems(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("itemId") itemId: Long,
        @Valid @RequestBody item: ItemDto
    ): ItemDto {
        return itemService.editItem(businessId, itemId, item)
    }

    @DeleteMapping("{itemId}")
    fun deleteItem(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("itemId") itemId: Long
    ) {
        return itemService.deleteItem(businessId, itemId)
    }
}
