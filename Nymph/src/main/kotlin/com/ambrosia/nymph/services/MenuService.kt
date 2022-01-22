package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.AddMenuItemDto
import com.ambrosia.nymph.dtos.EditMenuItemDto
import com.ambrosia.nymph.dtos.MenuDto
import com.ambrosia.nymph.entities.*
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toEntity
import com.ambrosia.nymph.repositories.*
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
            .orElseThrow { EntityNotFoundException(Business::class.java, "id", businessId) }
        val menu = menuDto.toEntity()
        menu.business = business
        return menuRepository.save(menu).toDto()
    }

    @Transactional
    fun editMenu(businessId: Long, menuId: Long, menuDto: MenuDto): MenuDto {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, "id", businessId) }
        val menu = menuRepository.findById(menuId)
            .orElseThrow { EntityNotFoundException(Menu::class.java, "id", menuId) }
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
            .orElseThrow { EntityNotFoundException(Business::class.java, "id", businessId) }
        val menu = menuRepository.findById(menuId)
            .orElseThrow { EntityNotFoundException(Menu::class.java, "id", menuId) }
        menuRepository.delete(menu)
    }

    @Transactional
    fun addItemToMenu(businessId: Long, menuId: Long, menuDto: AddMenuItemDto): MenuDto {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, "id", businessId) }
        val menu = menuRepository.findById(menuId)
            .orElseThrow { EntityNotFoundException(Menu::class.java, "id", businessId) }
        val category = categoryRepository.findById(menuDto.categoryId!!)
            .orElseThrow { EntityNotFoundException(Category::class.java, "id", menuDto.categoryId!!) }
        val item = itemRepository.findById(menuDto.itemId!!)
            .orElseThrow { EntityNotFoundException(Item::class.java, "id", menuDto.itemId!!) }
        menu.menuItems.add(MenuItem(extra = menuDto.extra, menu = menu, item = item, category = category))
        return menuRepository.save(menu).toDto()
    }

    @Transactional
    fun removeItemFromMenu(businessId: Long, menuId: Long, menuItemId: Long): MenuDto {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, "id", businessId) }
        val menu = menuRepository.findById(menuId)
            .orElseThrow { EntityNotFoundException(Menu::class.java, "id", businessId) }
        val menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow { EntityNotFoundException(MenuItem::class.java, "id", menuItemId) }
        menu.menuItems.remove(menuItem)
        return menuRepository.save(menu).toDto()
    }

    @Transactional
    fun editMenuItemExtra(businessId: Long, menuId: Long, menuItemId: Long, menuDto: EditMenuItemDto): MenuDto {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, "id", businessId) }
        val menu = menuRepository.findById(menuId)
            .orElseThrow { EntityNotFoundException(Menu::class.java, "id", businessId) }
        val menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow { EntityNotFoundException(MenuItem::class.java, "id", menuItemId) }
        menuItem.extra = menuDto.extra
        menuItemRepository.save(menuItem)
        return menu.toDto()
    }
}
