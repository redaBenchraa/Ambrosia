package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants
import com.ambrosia.nymph.constants.Constants.Companion.NAME_MAX_SIZE
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.Entity
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@SQLDelete(sql = "UPDATE item SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @field:NotNull(message = "error.item.id.null")
    var id: Long? = null,
    @field:NotNull(message = "error.item.name.null")
    @field:NotBlank(message = "error.item.name.blank")
    @field:Size(max = NAME_MAX_SIZE, message = "error.item.name.size.invalid")
    @Column(nullable = false)
    var name: String,
    @Column(columnDefinition = "text")
    var description: String? = null,
    var image: String? = null,
    @field:NotNull(message = "error.item.price.null")
    @field:Min(0, message = "error.item.price.negative")
    var price: Double,
    @Column(nullable = false)
    @ColumnDefault("false")
    var onlyForMenu: Boolean = false,
    @Column(nullable = false)
    @CreatedDate
    @ColumnDefault(Constants.NOW)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false)
    @LastModifiedDate
    @ColumnDefault(Constants.NOW)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    @Column(columnDefinition = "boolean default 0")
    var deleted: Boolean = false,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    var business: Business? = null,
)
