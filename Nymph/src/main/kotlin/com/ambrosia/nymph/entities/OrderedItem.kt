package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.NAME_MAX_SIZE
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@SQLDelete(sql = "UPDATE order_item SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class OrderItem(
    @field:NotNull(message = "error.orderItem.name.null")
    @field:NotBlank(message = "error.orderItem.name.blank")
    @field:Size(max = NAME_MAX_SIZE, message = "error.orderItem.name.size.invalid")
    @Column(nullable = false)
    var name: String,
    @Column(columnDefinition = "text")
    var description: String? = null,
    @field:NotNull(message = "error.orderItem.price.null")
    @field:Min(0, message = "error.orderItem.price.negative")
    var price: Double,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    var order: Order,
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "item_id", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonManagedReference
    var item: Item?,
) : BaseEntity()
