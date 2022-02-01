package com.ambrosia.nymph.dtos

import com.ambrosia.nymph.constants.DEFAULT_DOUBLE_VALUE
import com.ambrosia.nymph.constants.NAME_MAX_SIZE
import com.ambrosia.nymph.constants.PRICE_MIN
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class MenuDto(
    var id: Long?,
    @field:NotNull(message = "error.menu.name.null")
    @field:NotBlank(message = "error.menu.name.blank")
    @field:Size(max = NAME_MAX_SIZE, message = "error.menu.name.size.invalid")
    var name: String?,
    var description: String?,
    var image: String?,
    @field:NotNull(message = "error.menu.price.null")
    @field:Min(PRICE_MIN, message = "error.menu.price.negative")
    var price: Double?,
    var categories: MutableSet<MenuCategory> = HashSet(),
)

data class MenuCategory(
    var category: CategoryDto,
    var items: List<MenuItemDto>,
)

data class MenuItemDto(
    var id: Long,
    var item: ItemDto,
    var extra: Double = DEFAULT_DOUBLE_VALUE,
)
