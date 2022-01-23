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
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @field:NotNull(message = "error.order.id.null")
    var id: Long? = null,
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
    @JoinColumn(name = "session_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    var session: Session,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    var customer: Customer? = null,
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "order",
        targetEntity = OrderedItem::class
    )
    @JsonBackReference
    var orderedItem: MutableSet<OrderedItem> = HashSet(),
)
