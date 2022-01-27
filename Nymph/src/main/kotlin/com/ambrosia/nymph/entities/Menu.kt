package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Constants.Companion.NAME_MAX_SIZE
import com.ambrosia.nymph.constants.Constants.Companion.NOW
import com.ambrosia.nymph.constants.Constants.Companion.PRICE_MIN
import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.Hibernate
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@SQLDelete(sql = "UPDATE menu SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
data class Menu(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @field:NotNull(message = "error.menu.id.null")
    var id: Long? = null,
    @field:NotNull(message = "error.menu.name.null")
    @field:NotBlank(message = "error.menu.name.blank")
    @field:Size(max = NAME_MAX_SIZE, message = "error.menu.name.size.invalid")
    @Column(nullable = false)
    var name: String,
    @Column(columnDefinition = "text")
    var description: String? = null,
    var image: String? = null,
    @field:NotNull(message = "error.menu.price.null")
    @field:Min(PRICE_MIN, message = "error.menu.price.negative")
    var price: Double = PRICE_MIN.toDouble(),
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
    @OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "menu",
        targetEntity = MenuItem::class
    )
    @JsonBackReference
    var menuItems: MutableSet<MenuItem> = HashSet(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Menu

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
