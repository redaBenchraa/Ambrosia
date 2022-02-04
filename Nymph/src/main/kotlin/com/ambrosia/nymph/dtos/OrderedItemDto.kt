package com.ambrosia.nymph.dtos

class OrderedItemDto(
    var id: Long? = null,
    var name: String,
    var description: String? = null,
    var price: Double,
    var item: ItemDto,
)
