package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.NAME_MAX_SIZE
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.ColumnDefault
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

@Entity
@SQLDelete(sql = "UPDATE order_item SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class OrderItem(
    @Column(nullable = false, length = NAME_MAX_SIZE)
    var name: String,
    @Column(columnDefinition = "text")
    var description: String? = null,
    @ColumnDefault("0.0")
    @Column(nullable = false)
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
