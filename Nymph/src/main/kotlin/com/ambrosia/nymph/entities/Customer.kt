package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants
import com.fasterxml.jackson.annotation.JsonBackReference
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@SQLDelete(sql = "UPDATE customer SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @field:NotNull(message = "error.customer.id.null")
    var id: Long?,
    var firstName: String,
    var lastName: String,
    var age: String,
    @Column(nullable = false)
    @CreatedDate
    @ColumnDefault(Constants.NOW)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false)
    @LastModifiedDate
    @ColumnDefault(Constants.NOW)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    @Column(columnDefinition = "boolean default 0")
    var deleted: Boolean = false,
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "customer",
        targetEntity = Order::class
    )
    @JsonBackReference
    var orders: MutableSet<Order>,
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "customer",
        targetEntity = Bill::class
    )
    @JsonBackReference
    var bills: MutableSet<Bill>,
)
