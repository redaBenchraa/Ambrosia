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
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tables")
class Table(
	@Id
	@Column(nullable = false)
	@NotNull(message = "error.table.id.null")
	@GeneratedValue(strategy = GenerationType.AUTO)
	var id: String,
	@NotNull(message = "error.table.isAvailable.null")
	var isAvailable: Boolean = true,
	@NotNull(message = "error.table.number.null")
	var number: Int,
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
	@OneToMany(
		cascade = [CascadeType.ALL],
		fetch = FetchType.LAZY,
		mappedBy = "table"
	)
	@JsonBackReference
	var sessions: Set<Session>,
)
