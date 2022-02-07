package com.ambrosia.nymph.entities

import com.ambrosia.nymph.constants.Currency
import com.ambrosia.nymph.constants.EMAIL_MAX_SIZE
import com.ambrosia.nymph.constants.NAME_MAX_SIZE
import com.fasterxml.jackson.annotation.JsonBackReference
import org.hibernate.Hibernate
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

@Entity
@SQLDelete(sql = "UPDATE business SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
data class Business(
    @Column(nullable = false, length = NAME_MAX_SIZE)
    var name: String,
    @Column(nullable = false)
    var phoneNumber: String,
    @Column(nullable = false, length = EMAIL_MAX_SIZE)
    var email: String,
    @Column(columnDefinition = "text")
    var description: String? = null,
    var slogan: String? = null,
    var logo: String? = null,
    var location: String? = null,
    @Column(nullable = false)
    var currency: String = Currency.EUR.name,
    @Column(columnDefinition = "boolean default true", nullable = false)
    var available: Boolean = true,
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
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Business

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , deleted = $deleted , name = $name, email = $email)"
    }
}
