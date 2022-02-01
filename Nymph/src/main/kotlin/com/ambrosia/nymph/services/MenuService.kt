package com.ambrosia.nymph.services

import com.ambrosia.nymph.constants.DEFAULT_DOUBLE_VALUE
import com.ambrosia.nymph.dtos.AddMenuItemDto
import com.ambrosia.nymph.dtos.EditMenuItemDto
import com.ambrosia.nymph.dtos.MenuDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Category
import com.ambrosia.nymph.entities.Item
import com.ambrosia.nymph.entities.Menu
import com.ambrosia.nymph.entities.MenuItem
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toEntity
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.CategoryRepository
import com.ambrosia.nymph.repositories.ItemRepository
import com.ambrosia.nymph.repositories.MenuItemRepository
import com.ambrosia.nymph.repositories.MenuRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class MenuService(
    @Autowired private val businessRepository: BusinessRepository,
    @Autowired private val menuRepository: MenuRepository,
    @Autowired private val categoryRepository: CategoryRepository,
    @Autowired private val itemRepository: ItemRepository,
    @Autowired private val menuItemRepository: MenuItemRepository,
) {

    @Transactional
    fun addMenu(businessId: Long, menuDto: MenuDto): MenuDto {
        val business = businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val menu = menuDto.toEntity(business)
        return menuRepository.save(menu).toDto()
    }

    @Transactional
    fun editMenu(businessId: Long, menuId: Long, menuDto: MenuDto): MenuDto {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val menu = menuRepository.findById(menuId)
            .orElseThrow { EntityNotFoundException(Menu::class.java, mutableMapOf("id" to menuId)) }
        menuDto.name?.let { menu.name = it }
        menuDto.description?.let { menu.description = it }
        menuDto.image?.let { menu.image = it }
        menuDto.price?.let { menu.price = it }
        menuRepository.save(menu)
        return menu.toDto()
    }

    @Transactional
    fun deleteMenu(businessId: Long, menuId: Long) {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val menu = menuRepository.findById(menuId)
            .orElseThrow { EntityNotFoundException(Menu::class.java, mutableMapOf("id" to menuId)) }
        menuRepository.delete(menu)
    }

    @Transactional
    fun addItemToMenu(businessId: Long, menuId: Long, menuDto: AddMenuItemDto): MenuDto {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val menu = menuRepository.findById(menuId)
            .orElseThrow { EntityNotFoundException(Menu::class.java, mutableMapOf("id" to menuId)) }
        val category = categoryRepository.findById(menuDto.categoryId ?: -1)
            .orElseThrow {
                EntityNotFoundException(
                    Category::class.java,
                    mutableMapOf("id" to (menuDto.categoryId ?: -1))
                )
            }
        val item = itemRepository.findById(menuDto.itemId ?: -1)
            .orElseThrow { EntityNotFoundException(Item::class.java, mutableMapOf("id" to (menuDto.itemId ?: -1))) }
        menu.menuItems.add(MenuItem(
            extra = menuDto.extra ?: DEFAULT_DOUBLE_VALUE,
            menu = menu,
            item = item,
            category = category)
        )
        return menuRepository.save(menu).toDto()
    }

    @Transactional
    fun deleteMenuItem(businessId: Long, menuId: Long, menuItemId: Long): MenuDto {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val menu = menuRepository.findById(menuId)
            .orElseThrow { EntityNotFoundException(Menu::class.java, mutableMapOf("id" to menuId)) }
        val menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow { EntityNotFoundException(MenuItem::class.java, mutableMapOf("id" to menuItemId)) }
        menuItemRepository.delete(menuItem)
        menu.menuItems.remove(menuItem)
        return menu.toDto()
    }

    @Transactional
    fun editMenuItemExtra(businessId: Long, menuId: Long, menuItemId: Long, menuDto: EditMenuItemDto): MenuDto {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val menu = menuRepository.findById(menuId)
            .orElseThrow { EntityNotFoundException(Menu::class.java, mutableMapOf("id" to menuId)) }
        val menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow { EntityNotFoundException(MenuItem::class.java, mutableMapOf("id" to menuItemId)) }
        menuItem.extra = menuDto.extra ?: DEFAULT_DOUBLE_VALUE
        menuItemRepository.save(menuItem)
        return menu.toDto()
    }

    @Transactional
    fun getMenus(businessId: Long): List<MenuDto> {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        return menuRepository.findByBusinessId(businessId).stream().map { it.toDto() }.toList()
    }
}
