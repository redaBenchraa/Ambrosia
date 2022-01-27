package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants.Companion.EXTRA_MIN
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Entity
@SQLDelete(sql = "UPDATE menu_item SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class MenuItem(
    @field:NotNull(message = "error.menuItem.extra.null")
    @field:Min(EXTRA_MIN, message = "error.menuItem.price.negative")
    var extra: Double = EXTRA_MIN.toDouble(),
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    var menu: Menu? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    var category: Category? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    var item: Item? = null,
) : BaseEntity()
