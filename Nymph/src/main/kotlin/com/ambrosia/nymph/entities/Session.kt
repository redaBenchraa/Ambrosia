package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants.Companion.NOW
import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity
@SQLDelete(sql = "UPDATE session SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class Session(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @field:NotNull(message = "error.session.id.null")
    var id: Long? = null,
    @Column(nullable = false)
    @field:NotNull(message = "error.session.isPaid.null")
    var isPaid: Boolean = false,
    @Column(nullable = false)
    @field:NotNull(message = "error.session.isApproved.null")
    var isApproved: Boolean = true,
    @Column(nullable = false)
    @CreatedDate
    @ColumnDefault(NOW)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false)
    @LastModifiedDate
    @ColumnDefault(NOW)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    @Column(columnDefinition = "boolean default 0")
    var deleted: Boolean = false,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    var business: Business? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    var employee: Employee? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "table_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    var table: Table? = null,
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "session",
        targetEntity = Order::class
    )
    @JsonBackReference
    var orders: MutableSet<Order>,
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "session",
        targetEntity = Bill::class
    )
    @JsonBackReference
    var bills: MutableSet<Bill>,
)
