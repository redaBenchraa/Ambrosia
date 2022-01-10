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
