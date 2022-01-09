package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Currency
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
    var id: String,
    @NotNull(message = "error.business.name.null")
    @NotBlank(message = "error.business.name.blank")
    @Size(max = 24, message = "error.business.name.invalidSize")
    @Column(nullable = false)
    var name: String,
    @NotNull
    @NotBlank(message = "error.business.phoneNumber.blank")
    @Column(nullable = false, unique = true)
    var phoneNumber: String,
    @Column(nullable = false, unique = true)
    @NotBlank(message = "error.business.email.blank")
    @Email(message = "error.business.email.invalidFormat")
    @Size(max = 254, message = "error.business.email.invalidSize")
    var email: String,
    var description: String?,
    var slogan: String?,
    var logo: String?,
    var location: String?,
    @Column(nullable = false)
    @ColumnDefault("EUR")
    var currency: String = Currency.EUR.name,
    @Column(nullable = false)
    var isAvailable: Boolean = true,
    @Column(nullable = false)
    @CreatedDate
    @ColumnDefault("now")
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false)
    @LastModifiedDate
    @ColumnDefault("now")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var archivedAt: LocalDateTime? = null,
)

