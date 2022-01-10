package com.ambrosia.nymph.entities

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class Table(
    @Id
    @Column(nullable = false)
    @NotNull(message = "error.table.id.null")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: String,
    @NotNull(message = "error.employee.table.null")
    var number: Int,
)