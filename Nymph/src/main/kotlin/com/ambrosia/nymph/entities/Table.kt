package com.ambrosia.nymph.entities

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
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tables")
@SQLDelete(sql = "UPDATE tables SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class Table(
    @Column(nullable = false)
    @field:NotNull(message = "error.table.number.null")
    var number: Int,
    @field:NotNull(message = "error.table.available.null")
    @ColumnDefault("true")
    var available: Boolean = true,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    var business: Business,
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "table",
        targetEntity = Session::class
    )
    @JsonBackReference
    var sessions: MutableSet<Session> = HashSet(),
) : BaseEntity()
