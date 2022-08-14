package com.arconsis.domain.users

import com.arconsis.presentation.events.users.CreateUser
import java.util.*

data class User(
    val userId: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
)