package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants
import com.ambrosia.nymph.constants.Constants.Companion.NAME_MAX_SIZE
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class Item(
    @Id
    @Column(nullable = false)
    @NotNull(message = "error.item.id.null")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: String,
    @NotNull(message = "error.item.name.null")
    @NotBlank(message = "error.item.name.blank")
    @Size(max = NAME_MAX_SIZE, message = "error.item.name.invalidSize")
    @Column(nullable = false)
    var name: String,
    var description: String?,
    var image: String?,
    @NotNull(message = "error.item.price.null")
    @Min(0, message = "error.item.price.negative")
    var price: Double,
    var onlyForMenu: Boolean = false,
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