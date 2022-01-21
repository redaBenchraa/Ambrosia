package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.MenuDto
import com.ambrosia.nymph.services.MenuService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("business/{businessId}/menu")
class MenuController(@Autowired private val menuService: MenuService) {

    @PostMapping
    fun addMenus(
        @PathVariable("businessId") businessId: Long,
        @Valid @RequestBody menu: MenuDto
    ): MenuDto {
        return menuService.addMenu(businessId, menu)
    }

    @PutMapping("{menuId}")
    fun editMenus(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("menuId") menuId: Long,
        @Valid @RequestBody menu: MenuDto
    ): MenuDto {
        return menuService.editMenu(businessId, menuId, menu)
    }

    @DeleteMapping("{menuId}")
    fun deleteMenu(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("menuId") menuId: Long
    ) {
        return menuService.deleteMenu(businessId, menuId)
    }
}
