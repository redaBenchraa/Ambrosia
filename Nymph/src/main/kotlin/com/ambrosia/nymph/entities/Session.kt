package com.ambrosia.nymph.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.Hibernate
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
import javax.validation.constraints.NotNull

@Entity
@SQLDelete(sql = "UPDATE session SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
data class Session(
    @Column(nullable = false)
    @field:NotNull(message = "error.session.paid.null")
    var paid: Boolean = false,
    @Column(nullable = false)
    @field:NotNull(message = "error.session.closed.null")
    var closed: Boolean = false,
    @Column(nullable = false)
    @field:NotNull(message = "error.session.approved.null")
    var approved: Boolean = true,
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "business_id", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonManagedReference
    var business: Business? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "employee_id", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonManagedReference
    var employee: Employee? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "table_id", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonManagedReference
    var table: Table? = null,
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "session",
        targetEntity = Order::class
    )
    @JsonBackReference
    var orders: MutableSet<Order> = HashSet(),
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "session",
        targetEntity = Bill::class
    )
    @JsonBackReference
    var bills: MutableSet<Bill> = HashSet(),
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Session

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , deleted = $deleted )"
    }
}
