package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.ItemDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Item
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toEntity
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.ItemRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class ItemService(
    @Autowired private val businessRepository: BusinessRepository,
    @Autowired private val itemRepository: ItemRepository,
) {

    @Transactional
    fun addItem(businessId: Long, itemDto: ItemDto): ItemDto {
        val business = businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val item = itemDto.toEntity()
        item.business = business
        return itemRepository.save(item).toDto()
    }

    @Transactional
    fun editItem(businessId: Long, itemId: Long, itemDto: ItemDto): ItemDto {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val item = itemRepository.findById(itemId)
            .orElseThrow { EntityNotFoundException(Item::class.java, mutableMapOf("id" to itemId)) }
        itemDto.name?.let { item.name = it }
        itemDto.description?.let { item.description = it }
        itemDto.image?.let { item.image = it }
        itemDto.price?.let { item.price = it }
        itemDto.onlyForMenu?.let { item.onlyForMenu = it }
        itemRepository.save(item)
        return item.toDto()
    }

    @Transactional
    fun deleteItem(businessId: Long, itemId: Long) {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val item = itemRepository.findById(itemId)
            .orElseThrow { EntityNotFoundException(Item::class.java, mutableMapOf("id" to itemId)) }
        itemRepository.delete(item)
    }

    @Transactional
    fun getItems(businessId: Long): List<ItemDto> {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        return itemRepository.findByBusinessId(businessId).stream().map { it.toDto() }.toList()
    }
}
