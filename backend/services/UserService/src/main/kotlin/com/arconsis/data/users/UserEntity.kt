package com.arconsis.data.users

import com.arconsis.data.addresses.AddressEntity
import io.quarkus.security.jpa.Password
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity(name = "users")
class UserEntity(
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    var userId: UUID? = null,

    @Column(name = "first_name")
    var firstName: String,

    @Column(name = "last_name")
    var lastName: String,

    @Column
    var email: String,

    @Password
    @Column
    var password: String,

    @Column
    var username: String,

    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @Column(name = "updated_at")
    var updatedAt: Instant? = null,

    @OneToMany(mappedBy = "userId", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var addressEntities: MutableList<AddressEntity> = mutableListOf()
)