package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.NAME_MAX_SIZE
import com.ambrosia.nymph.constants.PRICE_MIN
import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
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
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@SQLDelete(sql = "UPDATE menu SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class Menu(
    @field:NotNull(message = "error.menu.name.null")
    @field:NotBlank(message = "error.menu.name.blank")
    @field:Size(max = NAME_MAX_SIZE, message = "error.menu.name.size.invalid")
    @Column(nullable = false)
    var name: String,
    @Column(columnDefinition = "text")
    var description: String? = null,
    var image: String? = null,
    @field:NotNull(message = "error.menu.price.null")
    @field:Min(PRICE_MIN, message = "error.menu.price.negative")
    var price: Double = PRICE_MIN.toDouble(),
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
