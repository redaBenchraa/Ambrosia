package com.ambrosia.nymph.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

@Entity
@SQLDelete(sql = "UPDATE customer SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class Customer(
    var firstName: String? = null,
    var lastName: String? = null,
    var age: Int? = null,
    var email: String? = null,
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "customer",
        targetEntity = Order::class
    )
    @JsonBackReference
    var orders: MutableSet<Order>? = HashSet(),
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "customer",
        targetEntity = Bill::class
    )
    @JsonBackReference
    var bills: MutableSet<Bill>? = HashSet(),
) : BaseEntity()
