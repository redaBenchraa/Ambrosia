package com.ambrosia.nymph.entities

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class Employee(
    @Id
    @Column(nullable = false)
    @NotNull(message = "error.employee.id.null")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: String,
    @NotNull(message = "error.employee.firstName.null")
    @NotBlank(message = "error.employee.firstName.blank")
    @Size(max = 128, message = "error.employee.firstName.invalidSize")
    var firstName: String,
    @NotNull(message = "error.employee.lastName.null")
    @NotBlank(message = "error.employee.lastName.blank")
    @Size(max = 128, message = "error.employee.lastName.invalidSize")
    var lastName: String,
)