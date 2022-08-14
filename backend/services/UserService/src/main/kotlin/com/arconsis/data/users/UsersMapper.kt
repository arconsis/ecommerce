package com.arconsis.data.users

import com.arconsis.data.addresses.toAddress
import com.arconsis.domain.users.User

fun UserEntity.toUser(): User {
    return User(
        userId = userId!!,
        firstName = firstName,
        lastName = lastName,
        email = email,
        username = username,
        addresses = addressEntities.map {
            it.toAddress()
        }
    )
}