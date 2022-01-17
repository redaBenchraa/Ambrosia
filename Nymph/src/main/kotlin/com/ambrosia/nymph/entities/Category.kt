package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants
import com.ambrosia.nymph.constants.Constants.Companion.NAME_MAX_SIZE
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class Category(
	@Id
	@Column(nullable = false)
	@NotNull(message = "error.category.id.null")
	@GeneratedValue(strategy = GenerationType.AUTO)
	var id: Long?,
	@NotNull(message = "error.item.category.null")
	@NotBlank(message = "error.item.category.blank")
	@Size(max = NAME_MAX_SIZE, message = "error.item.category.size.invalid")
	@Column(nullable = false)
	var name: String,
	@Column(columnDefinition = "text")
	var description: String?,
	var image: String?,
	@Column(nullable = false)
	@CreatedDate
	@ColumnDefault(Constants.NOW)
	var createdAt: LocalDateTime = LocalDateTime.now(),
	@Column(nullable = false)
	@LastModifiedDate
	@ColumnDefault(Constants.NOW)
	var updatedAt: LocalDateTime = LocalDateTime.now(),
	var deleted: Boolean = false,
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "business_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonManagedReference
	var business: Business,
)
