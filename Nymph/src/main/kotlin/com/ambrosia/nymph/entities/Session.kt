package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants.Companion.NOW
import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class Session(
    @Id
    @Column(nullable = false)
    @NotNull(message = "error.session.id.null")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: String,
    @Column(nullable = false)
    @NotNull(message = "error.session.isPaid.null")
    var isPaid: Boolean = false,
    @Column(nullable = false)
    @NotNull(message = "error.session.isApproved.null")
    var isApproved: Boolean = true,
    @Column(nullable = false)
    @CreatedDate
    @ColumnDefault(NOW)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false)
    @LastModifiedDate
    @ColumnDefault(NOW)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var archivedAt: LocalDateTime? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonManagedReference
    var business: Business,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonManagedReference
    var employee: Employee,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "table_id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonManagedReference
    var table: Table,
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "session"
    )
    @JsonBackReference
    var orders: Set<Order>,
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "session"
    )
    @JsonBackReference
    var bills: Set<Bill>,
)
