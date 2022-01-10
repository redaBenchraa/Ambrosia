package com.ambrosia.nymph.entities

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class Item(
    @Id
    @Column(nullable = false)
    @NotNull(message = "error.item.id.null")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: String,
    @NotNull(message = "error.item.name.null")
    @NotBlank(message = "error.item.name.blank")
    @Size(max = 128, message = "error.item.name.invalidSize")
    @Column(nullable = false)
    var name: String,
    var description: String?,
    var image: String?,
    @NotNull(message = "error.item.price.null")
    var price: Double,
    var onlyForMenu: Boolean = false,
)