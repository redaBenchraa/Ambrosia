package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
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