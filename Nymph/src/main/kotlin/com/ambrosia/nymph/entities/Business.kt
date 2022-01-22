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
    @field:NotNull(message = "error.business.id.null")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    @field:NotNull(message = "error.business.name.null")
    @field:NotBlank(message = "error.business.name.blank")
    @field:Size(max = NAME_MAX_SIZE, message = "error.business.name.size.invalid")
    @Column(nullable = false)
    var name: String,
    @field:NotNull
    @field:NotBlank(message = "error.business.phoneNumber.blank")
    @Column(nullable = false)
    var phoneNumber: String,
    @Column(nullable = false)
    @field:NotBlank(message = "error.business.email.blank")
    @field:Email(message = "error.business.email.format.invalid")
    @field:Size(max = EMAIL_MAX_SIZE, message = "error.business.email.size.invalid")
    var email: String,
    @Column(columnDefinition = "text")
    var description: String? = null,
    var slogan: String? = null,
    var logo: String? = null,
    var location: String? = null,
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
    var categories: MutableSet<Category> = HashSet(),
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "business",
        targetEntity = Employee::class
    )
    @JsonBackReference
    var employees: MutableSet<Employee> = HashSet(),
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "business",
        targetEntity = Table::class
    )
    @JsonBackReference
    var tables: MutableSet<Table> = HashSet(),
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "business",
        targetEntity = Item::class
    )
    @JsonBackReference
    var items: MutableSet<Item> = HashSet(),
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "business",
        targetEntity = Menu::class
    )
    @JsonBackReference
    var menus: MutableSet<Menu> = HashSet(),
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "business",
        targetEntity = Session::class
    )
    @JsonBackReference
    var sessions: MutableSet<Session> = HashSet(),
)
