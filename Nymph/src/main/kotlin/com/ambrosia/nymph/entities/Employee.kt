package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants.Companion.EMAIL_MAX_SIZE
import com.ambrosia.nymph.constants.Constants.Companion.NAME_MAX_SIZE
import com.ambrosia.nymph.constants.Constants.Companion.NOW
import com.ambrosia.nymph.constants.Role
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@SQLDelete(sql = "UPDATE employee SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
class Employee(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @field:NotNull(message = "error.employee.id.null")
    var id: Long? = null,
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
)
