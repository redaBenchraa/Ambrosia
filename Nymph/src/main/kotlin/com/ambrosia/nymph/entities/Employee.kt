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
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(indexes = [
    Index(columnList = "email")
])
@SQLDelete(sql = "UPDATE employee SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class Employee(
    @field:NotNull(message = "error.employee.firstName.null")
    @field:NotBlank(message = "error.employee.firstName.blank")
    @field:Size(max = NAME_MAX_SIZE, message = "error.employee.firstName.size.invalid")
    var firstName: String,
    @field:NotNull(message = "error.employee.lastName.null")
    @field:NotBlank(message = "error.employee.lastName.blank")
    @field:Size(max = NAME_MAX_SIZE, message = "error.employee.lastName.size.invalid")
    var lastName: String,
    @field:NotNull(message = "error.employee.email.null")
    @field:NotBlank(message = "error.employee.email.blank")
    @field:Size(max = EMAIL_MAX_SIZE, message = "error.employee.email.size.invalid")
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
