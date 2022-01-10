package com.ambrosia.nymph.entities

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class Bill(
    @Id
    @Column(nullable = false)
    @NotNull(message = "error.bill.id.null")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: String,
)