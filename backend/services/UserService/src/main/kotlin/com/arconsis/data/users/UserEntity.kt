package com.arconsis.data.users

import com.arconsis.data.addresses.AddressEntity
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity(name = "users")
@NamedQueries(
    NamedQuery(
        name = UserEntity.GET_USER_BY_SUB,
        query = """
			select us from users us
			where us.sub = :sub
		"""
    ),
)
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

    @Column
    var username: String,

    @Column(unique = true)
    var sub: String,

    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @Column(name = "updated_at")
    var updatedAt: Instant? = null,

    @OneToMany(mappedBy = "userId", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var addressEntities: MutableList<AddressEntity> = mutableListOf()
) {
    companion object {
        const val GET_USER_BY_SUB = "GET_USER_BY_SUB"
    }
}