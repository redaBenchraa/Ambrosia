package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants.Companion.NAME_MAX_SIZE
import com.ambrosia.nymph.constants.Constants.Companion.NOW
import com.ambrosia.nymph.constants.Constants.Companion.PRICE_MIN
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
class Menu(
    @Id
    @Column(nullable = false)
    @NotNull(message = "error.menu.id.null")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: String,
    @NotNull(message = "error.menu.name.null")
    @NotBlank(message = "error.menu.name.blank")
    @Size(max = NAME_MAX_SIZE, message = "error.menu.name.invalidSize")
    @Column(nullable = false)
    var name: String,
    var description: String?,
    var image: String?,
    @NotNull(message = "error.menu.price.null")
    @Min(PRICE_MIN, message = "error.menu.price.negative")
    var price: Double,
    @Column(nullable = false)
    @CreatedDate
    @ColumnDefault(NOW)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false)
    @LastModifiedDate
    @ColumnDefault(NOW)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var archivedAt: LocalDateTime? = null,
)
