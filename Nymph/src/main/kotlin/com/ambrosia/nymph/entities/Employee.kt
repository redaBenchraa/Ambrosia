package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
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
    @Column(nullable = false)
    @CreatedDate
    @ColumnDefault(Constants.NOW)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false)
    @LastModifiedDate
    @ColumnDefault(Constants.NOW)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var archivedAt: LocalDateTime? = null,
)
