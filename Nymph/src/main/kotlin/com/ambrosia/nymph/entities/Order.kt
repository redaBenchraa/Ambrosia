package com.ambrosia.nymph.entities

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class Order(
    @Id
    @Column(nullable = false)
    @NotNull(message = "error.order.id.null")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: String,
)