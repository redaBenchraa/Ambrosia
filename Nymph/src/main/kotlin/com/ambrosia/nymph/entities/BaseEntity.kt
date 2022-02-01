package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.NOW
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
class BaseEntity(
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    open var id: Long? = null,
    @Column(nullable = false)
    @CreatedDate
    @ColumnDefault(NOW)
    open var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false)
    @LastModifiedDate
    @ColumnDefault(NOW)
    open var updatedAt: LocalDateTime = LocalDateTime.now(),
    @Column(columnDefinition = "boolean default false")
    open var deleted: Boolean = false,
)
