package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.EMAIL_MAX_SIZE
import com.ambrosia.nymph.constants.NAME_MAX_SIZE
import com.ambrosia.nymph.constants.Role
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(indexes = [Index(columnList = "email")])
@SQLDelete(sql = "UPDATE employee SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class Employee(
    @Column(nullable = false, length = NAME_MAX_SIZE)
    var firstName: String,
    @Column(nullable = false, length = NAME_MAX_SIZE)
    var lastName: String,
    @Column(nullable = false, length = EMAIL_MAX_SIZE)
    var email: String,
    @Column(nullable = false)
    @field:NotNull(message = "error.employee.position.null")
    var position: Role = Role.MANAGER,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    var business: Business,
) : BaseEntity()
