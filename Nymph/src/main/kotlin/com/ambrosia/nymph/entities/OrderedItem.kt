package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants
import com.ambrosia.nymph.constants.Constants.Companion.NAME_MAX_SIZE
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class OrderedItem(
    @Id
    @Column(nullable = false)
    @NotNull(message = "error.orderedItem.id.null")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: String,
    @NotNull(message = "error.orderedItem.name.null")
    @NotBlank(message = "error.orderedItem.name.blank")
    @Size(max = NAME_MAX_SIZE, message = "error.orderedItem.name.invalidSize")
    @Column(nullable = false)
    var name: String,
    @Column(columnDefinition = "text")
    var description: String?,
    @NotNull(message = "error.orderedItem.price.null")
    @Min(0, message = "error.orderedItem.price.negative")
    var price: Double,
    @Column(nullable = false)
    @CreatedDate
    @ColumnDefault(Constants.NOW)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false)
    @LastModifiedDate
    @ColumnDefault(Constants.NOW)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var archivedAt: LocalDateTime? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonManagedReference
    var order: Order,
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonManagedReference
    var item: Item?,
)
