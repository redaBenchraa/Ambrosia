package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.MenuDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Menu
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toEntity
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.MenuRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional


@Service
class MenuService(
	@Autowired private val businessRepository: BusinessRepository,
	@Autowired private val menuRepository: MenuRepository
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
}