package com.ambrosia.nymph.entities

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class Category(
    @Id
    @Column(nullable = false)
    @NotNull(message = "error.category.id.null")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: String,
    @NotNull(message = "error.item.category.null")
    @NotBlank(message = "error.item.category.blank")
    @Size(max = 128, message = "error.item.category.invalidSize")
    @Column(nullable = false)
    var name: String,
    var description: String?,
    var image: String?,
)