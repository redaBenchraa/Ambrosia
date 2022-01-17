package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants.Companion.EMAIL_MAX_SIZE
import com.ambrosia.nymph.constants.Constants.Companion.NAME_MAX_SIZE
import com.ambrosia.nymph.constants.Constants.Companion.NOW
import com.ambrosia.nymph.constants.Role
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.Entity
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@SQLDelete(sql = "UPDATE employee SET deleted = true WHERE id=?")
@FilterDef(name = "deletedEmployeeFilter", parameters = [ParamDef(name = "isDeleted", type = "boolean")])
@Filter(name = "deletedEmployeeFilter", condition = "deleted = :isDeleted")
class Employee(
	@Id
	@Column(nullable = false)
	@field:NotNull(message = "error.employee.id.null")
	@GeneratedValue(strategy = GenerationType.AUTO)
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
	var deleted: Boolean = false,
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "business_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonManagedReference
	var business: Business? = null,
)
