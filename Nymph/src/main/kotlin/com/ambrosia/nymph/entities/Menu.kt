package com.ambrosia.nymph.entities

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class Menu(
    @Id
    @Column(nullable = false)
    @NotNull(message = "error.menu.id.null")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: String,
    @NotNull(message = "error.menu.name.null")
    @NotBlank(message = "error.menu.name.blank")
    @Size(max = 128, message = "error.menu.name.invalidSize")
    @Column(nullable = false)
    var name: String,
    var description: String?,
    var image: String?,
    @NotNull(message = "error.menu.price.null")
    var price: Double,
)