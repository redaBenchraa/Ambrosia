package com.ambrosia.nymph.entities

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
import javax.persistence.Table

@Entity
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class Order(
    @Column(columnDefinition = "boolean default false", nullable = false)
    var confirmed: Boolean = false,
    @Column(columnDefinition = "boolean default false", nullable = false)
    var approved: Boolean = false,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    var session: Session,
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "customer_id", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonManagedReference
    var customer: Customer? = null,
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "order",
        targetEntity = OrderItem::class
    )
    @JsonBackReference
    var orderItems: MutableSet<OrderItem> = HashSet(),
) : BaseEntity()
