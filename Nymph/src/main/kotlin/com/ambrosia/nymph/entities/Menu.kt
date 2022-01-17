package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants.Companion.NAME_MAX_SIZE
import com.ambrosia.nymph.constants.Constants.Companion.NOW
import com.ambrosia.nymph.constants.Constants.Companion.PRICE_MIN
import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class Menu(
	@Id
	@Column(nullable = false)
	@NotNull(message = "error.menu.id.null")
	@GeneratedValue(strategy = GenerationType.AUTO)
	var id: Long?,
	@NotNull(message = "error.menu.name.null")
	@NotBlank(message = "error.menu.name.blank")
	@Size(max = NAME_MAX_SIZE, message = "error.menu.name.size.invalid")
	@Column(nullable = false)
	var name: String,
	@Column(columnDefinition = "text")
	var description: String?,
	var image: String?,
	@NotNull(message = "error.menu.price.null")
	@Min(PRICE_MIN, message = "error.menu.price.negative")
	var price: Double,
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
	var business: Business,
	@OneToMany(
		cascade = [CascadeType.ALL],
		fetch = FetchType.LAZY,
		mappedBy = "menu",
		targetEntity = MenuItem::class
	)
	@JsonBackReference
	var menuItems: Set<MenuItem>,
)
