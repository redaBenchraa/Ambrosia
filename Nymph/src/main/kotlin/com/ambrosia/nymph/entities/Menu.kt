package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.DEFAULT_DOUBLE_VALUE
import com.ambrosia.nymph.constants.NAME_MAX_SIZE
import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
@SQLDelete(sql = "UPDATE menu SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class Menu(
    @Column(nullable = false, length = NAME_MAX_SIZE)
    var name: String,
    @Column(columnDefinition = "text")
    var description: String? = null,
    var image: String? = null,
    @Column(nullable = false)
    @ColumnDefault(value = DEFAULT_DOUBLE_VALUE.toString())
    var price: Double = DEFAULT_DOUBLE_VALUE,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    var business: Business,
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "menu",
        targetEntity = MenuItem::class
    )
    @JsonBackReference
    var menuItems: MutableSet<MenuItem> = HashSet(),
) : BaseEntity()
