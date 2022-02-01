package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.DEFAULT_DOUBLE_VALUE
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

@Entity
@SQLDelete(sql = "UPDATE bill SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class Bill(
    @Column(nullable = false)
    @ColumnDefault(value = DEFAULT_DOUBLE_VALUE.toString())
    var amount: Double = DEFAULT_DOUBLE_VALUE,
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "customer_id", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonManagedReference
    var customer: Customer? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "employee_id", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonManagedReference
    var employee: Employee? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "session_id", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonManagedReference
    var session: Session? = null,
) : BaseEntity()
