package com.ambrosia.nymph.entities

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class Customer(
    @Id
    @Column(nullable = false)
    @NotNull(message = "error.customer.id.null")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: String,
    var firstName: String,
    var lastName: String,
    var age: String,
)