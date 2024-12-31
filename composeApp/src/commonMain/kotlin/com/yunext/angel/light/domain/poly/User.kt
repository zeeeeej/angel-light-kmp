package com.yunext.angel.light.domain.poly

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val account: String,
    val root: String,
    val company: String,
    val userType: String,
    val token: String
)