package com.yunext.angel.light.domain

import com.yunext.angel.light.domain.poly.User
import kotlinx.coroutines.flow.Flow

val User.Companion.Empty by lazy {
    User("___IDLE_USER___", "", "", "", "", "")
}

val User.isEmpty: Boolean
    get() = this == User.Empty

interface UserDomain {

     val user: Flow<User>

    suspend fun saveUser(user: User)

}