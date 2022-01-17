package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants.Companion.EMAIL_MAX_SIZE
import com.ambrosia.nymph.constants.Constants.Companion.NAME_MAX_SIZE
import com.ambrosia.nymph.constants.Constants.Companion.NOW
import com.ambrosia.nymph.constants.Currency
import com.fasterxml.jackson.annotation.JsonBackReference
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class Business(
	@Id
	@Column(nullable = false)
	@NotNull(message = "error.business.id.null")
	@GeneratedValue(strategy = GenerationType.AUTO)
	var id: Long?,
	@NotNull(message = "error.business.name.null")
	@NotBlank(message = "error.business.name.blank")
	@Size(max = NAME_MAX_SIZE, message = "error.business.name.size.invalid")
	@Column(nullable = false)
	var name: String,
	@NotNull
	@NotBlank(message = "error.business.phoneNumber.blank")
	@Column(nullable = false, unique = true)
	var phoneNumber: String,
	@Column(nullable = false, unique = true)
	@NotBlank(message = "error.business.email.blank")
	@Email(message = "error.business.email.invalidFormat")
	@Size(max = EMAIL_MAX_SIZE, message = "error.business.email.size.invalid")
	var email: String,
	@Column(columnDefinition = "text")
	var description: String?,
	var slogan: String?,
	var logo: String?,
	var location: String?,
	@Column(nullable = false)
	var currency: String = Currency.EUR.name,
	@Column(nullable = false)
	var isAvailable: Boolean = true,
	@Column(nullable = false)
	@CreatedDate
	@ColumnDefault(NOW)
	var createdAt: LocalDateTime = LocalDateTime.now(),
	@Column(nullable = false)
	@LastModifiedDate
	@ColumnDefault(NOW)
	var updatedAt: LocalDateTime = LocalDateTime.now(),
	var deleted: Boolean = false,
	@OneToMany(
		cascade = [CascadeType.ALL],
		fetch = FetchType.LAZY,
		mappedBy = "business",
		targetEntity = Category::class
	)
	@JsonBackReference
	var categories: Set<Category> = HashSet(),
	@OneToMany(
		cascade = [CascadeType.ALL],
		fetch = FetchType.LAZY,
		mappedBy = "business",
		targetEntity = Employee::class
	)
	@JsonBackReference
	var employees: Set<Employee> = HashSet(),
	@OneToMany(
		cascade = [CascadeType.ALL],
		fetch = FetchType.LAZY,
		mappedBy = "business",
		targetEntity = Table::class
	)
	@JsonBackReference
	var tables: Set<Table> = HashSet(),
	@OneToMany(
		cascade = [CascadeType.ALL],
		fetch = FetchType.LAZY,
		mappedBy = "business",
		targetEntity = Item::class
	)
	@JsonBackReference
	var items: Set<Item> = HashSet(),
	@OneToMany(
		cascade = [CascadeType.ALL],
		fetch = FetchType.LAZY,
		mappedBy = "business",
		targetEntity = Menu::class
	)
	@JsonBackReference
	var menus: Set<Menu> = HashSet(),
	@OneToMany(
		cascade = [CascadeType.ALL],
		fetch = FetchType.LAZY,
		mappedBy = "business",
		targetEntity = Session::class
	)
	@JsonBackReference
	var sessions: Set<Session> = HashSet(),
)

