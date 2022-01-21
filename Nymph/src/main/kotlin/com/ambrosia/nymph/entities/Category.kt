package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Category(
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @Column(nullable = false) var name: String,
    @Column(columnDefinition = "text") var description: String?,
    var image: String?,
    var deleted: Boolean = false,
    @Column(nullable = false)
    @CreatedDate
    @ColumnDefault(Constants.NOW)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false)
    @LastModifiedDate
    @ColumnDefault(Constants.NOW)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonManagedReference
    var business: Business? = null,
)
