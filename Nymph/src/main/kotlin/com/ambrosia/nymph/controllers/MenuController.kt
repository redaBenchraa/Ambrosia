package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.AddMenuItemDto
import com.ambrosia.nymph.dtos.EditMenuItemDto
import com.ambrosia.nymph.dtos.MenuDto
import com.ambrosia.nymph.services.MenuService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("businesses/{businessId}/menus")
class MenuController(@Autowired private val menuService: MenuService) {

    @PostMapping
    fun addMenus(
        @PathVariable("businessId") businessId: Long,
        @Valid @RequestBody menu: MenuDto,
    ): MenuDto {
        return menuService.addMenu(businessId, menu)
    }

    @PutMapping("{menuId}")
    fun editMenus(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("menuId") menuId: Long,
        @Valid @RequestBody menu: MenuDto,
    ): MenuDto {
        return menuService.editMenu(businessId, menuId, menu)
    }

    @DeleteMapping("{menuId}")
    fun deleteMenu(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("menuId") menuId: Long,
    ) {
        menuService.deleteMenu(businessId, menuId)
    }

    @PostMapping("{menuId}/items")
    fun addItemToMenus(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("menuId") menuId: Long,
        @Valid @RequestBody menuItem: AddMenuItemDto,
    ): MenuDto {
        return menuService.addItemToMenu(businessId, menuId, menuItem)
    }

    @DeleteMapping("{menuId}/items/{menuItemId}")
    fun removeItemFromMenus(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("menuId") menuId: Long,
        @PathVariable("menuItemId") menuItemId: Long,
    ): MenuDto {
        return menuService.deleteMenuItem(businessId, menuId, menuItemId)
    }

    @PutMapping("{menuId}/items/{menuItemId}")
    fun editItemToMenus(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("menuId") menuId: Long,
        @PathVariable("menuItemId") menuItemId: Long,
        @Valid @RequestBody menuItem: EditMenuItemDto,
    ): MenuDto {
        return menuService.editMenuItemExtra(businessId, menuId, menuItemId, menuItem)
    }

    @GetMapping
    fun getMenu(@PathVariable("businessId") businessId: Long): List<MenuDto> {
        return menuService.getMenus(businessId)
    }
}
